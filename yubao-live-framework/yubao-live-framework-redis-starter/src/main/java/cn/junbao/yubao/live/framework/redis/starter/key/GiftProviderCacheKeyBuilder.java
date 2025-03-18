package cn.junbao.yubao.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Junbao
 * @Date: 2025/3/6 20:45
 * @Description:
 */
@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class GiftProviderCacheKeyBuilder extends RedisKeyBuilder {

    private static final String GIFT_CONFIG_CACHE = "gift_config_cache";
    private static final String GIFT_LIST_CACHE = "gift_list_cache";
    private static final String GIFT_MQ_CONSUME_KEY = "gift_mq_consume_key";
    private static final String GIFT_LIST_LOCK = "gift_list_lock";
    private static final String LIVING_PK_KEY = "living_pk_key";
    private static final String LIVING_PK_SEND_SEQ = "living_pk_send_seq";
    private static final String LIVING_PK_IS_OVER = "living_pk_is_over";

    public String buildGiftConfigCacheKey(Integer giftId) {
        return super.getPrefix() + GIFT_CONFIG_CACHE + super.getSplitItem() + giftId;
    }
    public String buildGiftListCacheKey() {
        return super.getPrefix() + GIFT_LIST_CACHE ;
    }
    public String buildGiftListLockCacheKey() {
        return super.getPrefix() + GIFT_LIST_LOCK ;
    }

    public String buildeGiftMqConsumeKey(String uuid) {
        return super.getPrefix() + GIFT_MQ_CONSUME_KEY + super.getSplitItem() + uuid;
    }

    public String buildLivingPkIsOver(Integer roomId) {
        return super.getPrefix() + LIVING_PK_IS_OVER + super.getSplitItem() + roomId;
    }

    public String buildLivingPkSendSeq(Integer roomId) {
        return super.getPrefix() + LIVING_PK_SEND_SEQ + super.getSplitItem() + roomId;
    }

    public String buildLivingPkKey(Integer roomId) {
        return super.getPrefix() + LIVING_PK_KEY + super.getSplitItem() + roomId;
    }

}
