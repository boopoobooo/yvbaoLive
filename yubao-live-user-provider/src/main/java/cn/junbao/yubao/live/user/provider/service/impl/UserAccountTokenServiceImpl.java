package cn.junbao.yubao.live.user.provider.service.impl;

import cn.junbao.yubao.live.framework.redis.starter.key.AccountProviderCacheKeyBuilder;
import cn.junbao.yubao.live.user.provider.service.IUserAccountTokenService;
import com.alibaba.cloud.commons.lang.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserAccountTokenServiceImpl implements IUserAccountTokenService {

    @Resource
    private RedisTemplate<String,String> redisTemplate;
    @Resource
    private AccountProviderCacheKeyBuilder accountProviderCacheKeyBuilder;

    @Override
    public String createAndSaveToken(Long userId) {
        if (userId == null ){
            log.warn("[createAndSaveToken] 当前userid == null !");
            return null;
        }
        String token = UUID.randomUUID().toString();
        String cacheKey = accountProviderCacheKeyBuilder.buildAccountTokenKey(token);
        redisTemplate.opsForValue().set(cacheKey, String.valueOf(userId),5, TimeUnit.DAYS);

        return token;
    }

    @Override
    public Long getUserIdByToken(String token) {
        String cache = accountProviderCacheKeyBuilder.buildAccountTokenKey(token);
        String userIdStr = redisTemplate.opsForValue().get(cache);
        if (StringUtils.isBlank(userIdStr)){
            log.warn("[getUserIdByToken] 获取到的userid == null !");
            return null;
        }
        return Long.valueOf(userIdStr);
    }
}
