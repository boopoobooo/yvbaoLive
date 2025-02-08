package cn.junbao.yubao.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class MsgProviderCacheKeyBuilder extends RedisKeyBuilder{
    private static String SMS_LOGIN_KEY = "smsLoginKey";

    public String buildSmsLoginKey(String phone) {
        return super.getPrefix() + SMS_LOGIN_KEY + super.getSplitItem() + phone;
    }
}
