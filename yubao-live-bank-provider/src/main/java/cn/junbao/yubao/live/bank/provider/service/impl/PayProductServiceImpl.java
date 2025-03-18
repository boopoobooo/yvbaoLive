package cn.junbao.yubao.live.bank.provider.service.impl;

import cn.junbao.yubao.live.bank.dto.PayProductDTO;
import cn.junbao.yubao.live.bank.provider.dao.mapper.IPayProductMapper;
import cn.junbao.yubao.live.bank.provider.dao.po.PayProductPO;
import cn.junbao.yubao.live.bank.provider.service.IPayProductService;
import cn.junbao.yubao.live.common.interfaces.enums.CommonStatusEum;
import cn.junbao.yubao.live.common.interfaces.utils.ConvertBeanUtils;
import cn.junbao.yubao.live.framework.redis.starter.key.BankProviderCacheKeyBuilder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: Junbao
 * @Date: 2025/3/10 20:19
 * @Description:
 */
@Service
@Slf4j
public class PayProductServiceImpl implements IPayProductService {
    @Resource
    private IPayProductMapper payProductMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private BankProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public List<PayProductDTO> products(Integer type) {
        String cacheKey = cacheKeyBuilder.buildPayProductCache(type);
        List<PayProductDTO> cacheList = redisTemplate.opsForList().range(cacheKey, 0, 30).stream().map(x -> (PayProductDTO) x).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(cacheList)) {
            if (cacheList.get(0).getId() == null) {
                log.warn("[products]返回空集合");
                return Collections.emptyList();
            }
            log.info("[products] 返回cacheList.size = {}",cacheList.size());
            return cacheList;
        }
        List<PayProductPO> payProductPOS = payProductMapper.selectListByType(type,CommonStatusEum.VALID_STATUS.getCode());
        List<PayProductDTO> payProductDTOS = ConvertBeanUtils.convertList(payProductPOS, PayProductDTO.class);
        if (CollectionUtils.isEmpty(payProductDTOS)) {
            //空值缓存
            redisTemplate.opsForList().leftPush(cacheKey, new PayProductDTO());
            redisTemplate.expire(cacheKey, 1L, TimeUnit.MINUTES);
            return Collections.emptyList();
        }
        // List类型的putAll放入集合有bug，需要转换为数组
        redisTemplate.opsForList().leftPushAll(cacheKey, payProductDTOS.toArray());
        redisTemplate.expire(cacheKey, 30L, TimeUnit.MINUTES);
        return payProductDTOS;
    }

    @Override
    public PayProductDTO getByProductId(Integer productId) {
        String cacheKey = cacheKeyBuilder.buildPayProductItemCache(productId);
        PayProductDTO payProductDTO = (PayProductDTO) redisTemplate.opsForValue().get(cacheKey);
        if (payProductDTO != null) {
            if (payProductDTO.getId() == null) {
                return null;
            }
            return payProductDTO;
        }

        PayProductPO payProductPO = payProductMapper.selectByProductId(productId);
        payProductDTO = ConvertBeanUtils.convert(payProductPO, PayProductDTO.class);

        if (payProductDTO == null) {
            redisTemplate.opsForValue().set(cacheKey, new PayProductDTO(), 1L, TimeUnit.MINUTES);
            return null;
        }
        redisTemplate.opsForValue().set(cacheKey, payProductDTO, 30L, TimeUnit.MINUTES);
        return payProductDTO;
    }
}
