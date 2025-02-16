package cn.junbao.yubao.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 */
@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class ImCoreServerCacheKeyBuilder extends RedisKeyBuilder {

    private static final String IM_USER_ONLINE_RECORD = "imUserOnlineRecord";

    public String buildImUserOnline(Integer appId,Long userId ) {
        return super.getPrefix() + IM_USER_ONLINE_RECORD + super.getSplitItem()+appId + super.getSplitItem()+userId % 10000;
    }


}
