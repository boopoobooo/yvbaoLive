package cn.junbao.yubao.live.im.provider.service.impl;

import cn.junbao.yubao.live.framework.redis.starter.key.ImProviderCacheKeyBuilder;
import cn.junbao.yubao.live.im.provider.service.ImTokenService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ImTokenServiceImpl implements ImTokenService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private ImProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public String createImLoginToken(long userId, int appId) {
        String token = UUID.randomUUID() + "%" + appId;
        redisTemplate.opsForValue().set(cacheKeyBuilder.buildImLoginTokenKey(token), userId, 5, TimeUnit.MINUTES);
        log.info("[createImLoginToken] userId = {},appId = {},token ={}",userId,appId,token);
        return token;
    }

    @Override
    public Long getUserIdByToken(String token) {
        Object userId = redisTemplate.opsForValue().get(cacheKeyBuilder.buildImLoginTokenKey(token));
        return userId == null ? null : Long.valueOf((Integer) userId);
    }
}
