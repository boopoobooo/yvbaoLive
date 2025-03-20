package cn.junbao.yubao.live.gift.provider.service.impl;

import cn.junbao.yubao.live.bank.interfaces.ICurrencyAccountRpc;
import cn.junbao.yubao.live.common.interfaces.topic.GiftProviderTopicNames;
import cn.junbao.yubao.live.common.interfaces.utils.ListUtils;
import cn.junbao.yubao.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import cn.junbao.yubao.live.gift.bo.SendRedPacketBO;
import cn.junbao.yubao.live.gift.constants.RedPacketStatusEnum;
import cn.junbao.yubao.live.gift.dto.RedPacketConfigReqDTO;
import cn.junbao.yubao.live.gift.dto.RedPacketReceiveDTO;
import cn.junbao.yubao.live.gift.provider.dao.mapper.IRedPacketConfigMapper;
import cn.junbao.yubao.live.gift.provider.dao.po.RedPacketConfigPO;
import cn.junbao.yubao.live.gift.provider.service.IRedPacketConfigService;
import cn.junbao.yubao.live.im.constants.AppIdEnum;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import cn.junbao.yubao.live.im.router.enums.ImMsgBizCodeEnum;
import cn.junbao.yubao.live.im.router.interfaces.ImRouterRpc;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomReqDTO;
import cn.junbao.yubao.live.living.interfaces.rpc.ILivingRoomRpc;
import cn.junbao.yubao.live.mq.starter.publisher.EventPublisher;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Junbao
 * @Date: 2025/3/18 17:22
 * @Description:
 */
@Service
@Slf4j
public class RedPacketConfigServiceImpl implements IRedPacketConfigService {
    @Resource
    private IRedPacketConfigMapper redPacketConfigMapper;
    @Resource
    private RedisTemplate<String ,Object> redisTemplate;

    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;
    @DubboReference(check = false)
    private ILivingRoomRpc livingRoomRpc;

    @DubboReference(check = false)
    private ImRouterRpc imRouterRpc;
    @DubboReference(check = false)
    private ICurrencyAccountRpc currencyAccountRpc;
    @Resource
    private EventPublisher eventPublisher;

    @Override
    public RedPacketConfigPO queryByAnchorId(Long anchorId) {
        RedPacketConfigPO redPacketConfigReqPO = new RedPacketConfigPO();
        redPacketConfigReqPO.setAnchorId(anchorId);
        redPacketConfigReqPO.setStatus(RedPacketStatusEnum.WAIT.getCode());
        RedPacketConfigPO redPacketConfigPO = redPacketConfigMapper.selectOne(redPacketConfigReqPO);
        return redPacketConfigPO;
    }

    @Override
    public RedPacketConfigPO queryByConfigCode(String code) {

        RedPacketConfigPO redPacketConfigReqPO = new RedPacketConfigPO();
        redPacketConfigReqPO.setConfigCode(code);
        redPacketConfigReqPO.setStatus(RedPacketStatusEnum.IS_PREPARED.getCode());
        return redPacketConfigMapper.selectOne(redPacketConfigReqPO);
    }

    @Override
    public boolean addOne(RedPacketConfigPO redPacketConfigPO) {
        redPacketConfigPO.setConfigCode(UUID.randomUUID().toString());
        return redPacketConfigMapper.insert(redPacketConfigPO) > 0;
    }

    @Override
    public boolean updateById(RedPacketConfigPO redPacketConfigPO) {
        return redPacketConfigMapper.updateById(redPacketConfigPO) > 0;
    }

    @Override
    public boolean prepareRedPacket(Long anchorId) {

        RedPacketConfigPO redPacketConfigPO = this.queryByAnchorId(anchorId);
        if (redPacketConfigPO == null ){
            //防止重复生成，以及错误参数传递情况
            log.warn("[prepareRedPacket] redPacketConfigPO为空或 最近一次红包雨已失效,anchorId= {}",anchorId);
            return false;
        }
        // 加锁保证原子性：仿重
        Boolean isLock = redisTemplate.opsForValue().setIfAbsent(cacheKeyBuilder.buildRedPacketInitLock(redPacketConfigPO.getConfigCode()), 1, 3L, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(isLock)) {
            return false;
        }

        Integer totalPrice = redPacketConfigPO.getTotalPrice();
        Integer totalCount = redPacketConfigPO.getTotalCount();
        List<Integer> priceList = this.createRedPacketPriceList(totalPrice, totalCount);
        String cacheKey = cacheKeyBuilder.buildRedPacketList(redPacketConfigPO.getConfigCode());
        // 将红包数据拆分为子集合进行插入到Redis，避免 Redis输入输出缓冲区 被填满
        List<List<Integer>> splitPriceList = ListUtils.splistList(priceList, 100);//每次为100个
        for (List<Integer> priceItemList : splitPriceList) {
            redisTemplate.opsForList().leftPushAll(cacheKey, priceItemList.toArray());
        }
        // 更改红包雨配置状态，防止重发
        redPacketConfigPO.setStatus(RedPacketStatusEnum.IS_PREPARED.getCode());
        this.updateById(redPacketConfigPO);//更新数据库
        // Redis中设置该红包雨已经准备好的标记
        redisTemplate.opsForValue().set(cacheKeyBuilder.buildRedPacketPrepareSuccess(redPacketConfigPO.getConfigCode()), 1, 1L, TimeUnit.DAYS);
        return true;
    }

    /**
     * 二倍均值法：
     * 创建红包雨的每个红包金额数据
     */
    private List<Integer> createRedPacketPriceList(Integer totalPrice, Integer totalCount) {
        List<Integer> redPacketPriceList = new ArrayList<>();
        for (int i = 0; i < totalCount; i++) {
            if (i + 1 == totalCount) {
                // 如果是最后一个红包
                redPacketPriceList.add(totalPrice);
                break;
            }
            int maxLimit = (totalPrice / (totalCount - i)) * 2;// 最大限额为平均值的两倍
            int currentPrice = ThreadLocalRandom.current().nextInt(1, maxLimit);
            totalPrice -= currentPrice;
            redPacketPriceList.add(currentPrice);
        }
        return redPacketPriceList;
    }

    @Override
    public RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO redPacketConfigReqDTO) {
        String code = redPacketConfigReqDTO.getRedPacketConfigCode();
        String redPacketListCacheKey = cacheKeyBuilder.buildRedPacketList(code);
        Object priceObj = redisTemplate.opsForList().rightPop(redPacketListCacheKey);
        if (priceObj == null){
            log.warn("[receiveRedPacket] 红包雨未开始或已结束");
            return null;
        }

        Integer price = (Integer) priceObj;
        // 发送mq消息进行异步信息的统计，以及用户余额的增加
        SendRedPacketBO sendRedPacketBO = new SendRedPacketBO();
        sendRedPacketBO.setPrice(price);
        sendRedPacketBO.setReqDTO(redPacketConfigReqDTO);
        eventPublisher.publish(GiftProviderTopicNames.RECEIVE_RED_PACKET,JSON.toJSONString(sendRedPacketBO));
        return new RedPacketReceiveDTO(price,"恭喜抢到"+price+"的金币");
    }

    @Override
    public Boolean startRedPacket(RedPacketConfigReqDTO reqDTO) {
        String code = reqDTO.getRedPacketConfigCode();
        // 红包没有准备好，则返回false
        if (Boolean.FALSE.equals(redisTemplate.hasKey(cacheKeyBuilder.buildRedPacketPrepareSuccess(code)))) {
            log.warn("[startRedPacket]红包雨还没有准备好，请等待红包雨准备完成..");
            return false;
        }
        // 红包已经开始过（有别的线程正在通知用户中），返回false
        String notifySuccessCacheKey = cacheKeyBuilder.buildRedPacketNotify(code);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(notifySuccessCacheKey))) {
            log.warn("[startRedPacket] 当前红包雨配置已经通知过直播间用户，请勿重复通知，code:{}", code);
            return false;
        }

        redisTemplate.opsForValue().set(notifySuccessCacheKey, 1, 1L, TimeUnit.DAYS);
        // 广播通知直播间所有用户开始抢红包了
        RedPacketConfigPO redPacketConfigPO = this.queryByConfigCode(code);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("redPacketConfig", JSON.toJSONString(redPacketConfigPO));//TODO 此次应该修改为对应的dto返回对应需要的消息即可
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setRoomId(reqDTO.getRoomId());
        livingRoomReqDTO.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
        List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(livingRoomReqDTO);
        if (CollectionUtils.isEmpty(userIdList)) {
            log.warn("[startRedPacket] 直播间没有用户，code:{}", code);
        }
        //发送im消息
        this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.RED_PACKET_CONFIG.getCode(), jsonObject);
        redPacketConfigPO.setStatus(RedPacketStatusEnum.IS_SEND.getCode());
        this.updateById(redPacketConfigPO);
        return true;

    }

    @Override
    public void receiveRedPacketHandler(RedPacketConfigReqDTO reqDTO, Integer price) {
        log.info("[receiveRedPacketHandler] 当前用户抢到红包  userid = {},price = {}",reqDTO.getUserId(),price);
        String code = reqDTO.getRedPacketConfigCode();
        String totalGetCountCacheKey = cacheKeyBuilder.buildRedPacketTotalGetCount(code);
        String totalGetPriceCacheKey = cacheKeyBuilder.buildRedPacketTotalGetPrice(code);
        // 记录该用户总共领取了多少金额的红包
        //将本次抢到的金额 + 到总金额中去(当前用户已经抢到的红包总金额)
        redisTemplate.opsForValue().increment(cacheKeyBuilder.buildUserTotalGetPrice(reqDTO.getUserId()), price);
        //增加  整场红包雨中已经抢过的 红包数量
        redisTemplate.opsForHash().increment(totalGetCountCacheKey, reqDTO.getUserId(), 1);
        redisTemplate.expire(totalGetCountCacheKey, 1L, TimeUnit.DAYS);
        //增加 本次红包雨中已经抢到的 红包金额
        redisTemplate.opsForHash().increment(totalGetPriceCacheKey, reqDTO.getUserId(), price);
        redisTemplate.expire(totalGetPriceCacheKey, 1L, TimeUnit.DAYS);

        // 往用户的余额里增加金额
        currencyAccountRpc.incr(reqDTO.getUserId(), price);
        // 持久化红包雨 的 totalGetCount 和 totalGetPrice
        //redPacketConfigMapper.incrTotalGetPrice(code, price);//todo 更新数据库
        //redPacketConfigMapper.incrTotalGetCount(code);

    }

    /**
     * 批量发送im消息
     */
    private void batchSendImMsg(List<Long> userIdList, String bizCode, JSONObject jsonObject) {
        List<ImMsgBody> imMsgBodies = new ArrayList<>();

        userIdList.forEach(userId -> {
            ImMsgBody imMsgBody = new ImMsgBody();
            imMsgBody.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
            imMsgBody.setBizCode(bizCode);
            imMsgBody.setData(jsonObject.toJSONString());
            imMsgBody.setUserId(userId);
            imMsgBodies.add(imMsgBody);
        });
        imRouterRpc.batchSendMsg(imMsgBodies);
    }
}
