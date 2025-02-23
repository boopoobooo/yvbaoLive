package cn.junbao.yubao.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 */
@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class LivingProviderCacheKeyBuilder extends RedisKeyBuilder {

    private static final String LIVING_ROOM_LIST = "living_room_list";
    private static final String LIVING_ROOM_OBJ = "living_room_obj";
    private static final String REFRESH_LIVING_ROOM_LIST_LOCK = "refresh_living_room_list_lock";


    public String buildLivingRoomListKey(Integer type) {
        return super.getPrefix() + LIVING_ROOM_LIST + super.getSplitItem() + type;
    }
    public String buildLivingRoomObjKey(Long roomId) {
        return super.getPrefix() + LIVING_ROOM_OBJ + super.getSplitItem() + roomId;
    }
    public String buildRefreshLivingRoomListLock( ){
        return super.getPrefix() + REFRESH_LIVING_ROOM_LIST_LOCK + super.getSplitItem();

    }


}
