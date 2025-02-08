package cn.junbao.yubao.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class OtherCacheKeyBuilder extends RedisKeyBuilder {

    private static String USER_INFO_KEY = "other-";

    public String buildUserInfoKey(Long userId) {
        return super.getPrefix() + USER_INFO_KEY + super.getSplitItem() + userId;
    }


}