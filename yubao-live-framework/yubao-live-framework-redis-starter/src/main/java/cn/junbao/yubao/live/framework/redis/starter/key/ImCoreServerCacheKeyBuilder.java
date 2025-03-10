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
    private static final String IM_MSG_ACK_MAP = "im_Msg_ACK_Map";

    public String buildImUserOnline(Integer appId,Long userId ) {
        return super.getPrefix() + IM_USER_ONLINE_RECORD + super.getSplitItem()+appId + super.getSplitItem()+userId;
    }

    public String buildImMsgAckMapKey(Integer appId,Long userId ) {
        return super.getPrefix()+IM_MSG_ACK_MAP+super.getSplitItem()+appId+super.getSplitItem()+userId;
    }


}
