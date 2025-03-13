package cn.junbao.yubao.live.bank.provider.service.impl;

import cn.junbao.yubao.live.bank.dto.AccountTradeReqDTO;
import cn.junbao.yubao.live.bank.dto.AccountTradeRespDTO;
import cn.junbao.yubao.live.bank.provider.dao.mapper.ICurrencyAccountMapper;
import cn.junbao.yubao.live.bank.provider.dao.po.CurrencyAccountPO;
import cn.junbao.yubao.live.bank.provider.service.ICurrencyAccountService;
import cn.junbao.yubao.live.bank.provider.service.ICurrencyTradeService;
import cn.junbao.yubao.live.common.interfaces.enums.TradeTypeEnum;
import cn.junbao.yubao.live.framework.redis.starter.key.BankProviderCacheKeyBuilder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CurrencyAccountServiceImpl implements ICurrencyAccountService {

    @Resource
    private ICurrencyAccountMapper currencyAccountMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private BankProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private ICurrencyTradeService currencyTradeService;

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));

    @Override
    public boolean insertOne(long userId) {
        try {
            CurrencyAccountPO accountPO = new CurrencyAccountPO();
            accountPO.setUserId(userId);
            currencyAccountMapper.insertOne(accountPO);
            return true;
        } catch (Exception e) {
            //有异常但是不抛出，只为了避免重复创建相同userId的账户
        }
        return false;
    }

    @Override
    public void incr(long userId, int num) {
        String cacheKey = cacheKeyBuilder.buildUserBalance(userId);
        if (redisTemplate.hasKey(cacheKey)) {
            redisTemplate.opsForValue().increment(cacheKey, num);
            redisTemplate.expire(cacheKey, 5, TimeUnit.MINUTES);
        }
        threadPoolExecutor.execute(() -> {
            //在异步线程池中完成数据库层的扣减和流水记录插入操作，带有事务
            this.consumeIncrDBHandler(userId, num);
        });

    }

    @Override
    public Integer getBalance(long userId) {
        String cacheKey = cacheKeyBuilder.buildUserBalance(userId);
        Object cacheBalance = redisTemplate.opsForValue().get(cacheKey);
        if (cacheBalance != null) {
            if ((Integer) cacheBalance == -1) {
                //空值缓存,防止缓存穿透
                return null;
            }
            return (Integer) cacheBalance;
        }
        Integer currentBalance = currencyAccountMapper.queryBalance(userId);
        log.info("[getBalance] userId = {},当前余额: {}",userId,currentBalance);
        if (currentBalance == null) {
            redisTemplate.opsForValue().set(cacheKey, -1, 1, TimeUnit.MINUTES);
            return null;
        }
        redisTemplate.opsForValue().set(cacheKey, currentBalance, 30, TimeUnit.MINUTES);
        return currentBalance;
    }


    @Override
    public void decr(long userId, int num) {
        //扣减余额
        String cacheKey = cacheKeyBuilder.buildUserBalance(userId);
        if (redisTemplate.hasKey(cacheKey)) {
            //基于redis的扣减操作
            redisTemplate.opsForValue().decrement(cacheKey, num);
            redisTemplate.expire(cacheKey, 5, TimeUnit.MINUTES);
        }
        threadPoolExecutor.execute(() -> {
            //在异步线程池中完成数据库层的扣减和流水记录插入操作，带有事务
            consumeDecrDBHandler(userId, num);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void consumeIncrDBHandler(long userId, int num) {
        //更新db，插入db
        currencyAccountMapper.incr(userId, num);
        //流水记录
        currencyTradeService.insertOne(userId, num, TradeTypeEnum.SEND_GIFT_TRADE.getCode());
    }

    @Transactional(rollbackFor = Exception.class)
    public void consumeDecrDBHandler(long userId, int num) {
        //更新db，插入db
        currencyAccountMapper.decr(userId, num);
        //流水记录
        currencyTradeService.insertOne(userId, num * -1, TradeTypeEnum.SEND_GIFT_TRADE.getCode());
    }

    @Override
    public AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO)  {
        // 1 余额判断并在Redis中扣减余额
        Long userId = accountTradeReqDTO.getUserId();
        int num = accountTradeReqDTO.getNum();
        String lockKey = cacheKeyBuilder.buildBalanceLockKey(userId);
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent(lockKey, 1, 1L, TimeUnit.SECONDS);
        if (isLock){
            try {
                Integer balance = getBalance(userId);
                if (balance == null || balance < num ){
                    log.warn("[consumeForSendGift] 余额不足，userId = {}",userId);
                    return AccountTradeRespDTO.buildFail(userId,"余额不足",0);
                }
                //进行账户扣减(原子扣减,异步更新DB)
                this.decr(userId,num);
            } finally {
                redisTemplate.delete(lockKey);
            }
        }else {
            //进行重试
            try {
                Thread.sleep(ThreadLocalRandom.current().nextLong(500,1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.consumeForSendGift(accountTradeReqDTO);
        }

        return AccountTradeRespDTO.buildSuccess(userId, "扣费成功");
    }
}
