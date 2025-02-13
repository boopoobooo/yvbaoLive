package cn.junbao.yubao.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 */
@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class AccountProviderCacheKeyBuilder extends RedisKeyBuilder {

    private static final String ACCOUNT_TOKEN_KEY = "accountTokenKey";

    public String buildAccountTokenKey (String token) {
        return super.getPrefix() + ACCOUNT_TOKEN_KEY + super.getSplitItem() + token;
    }


}
