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

    /**
     * 红包雨部分
     */
    private static final String RED_PACKET_LIST = "red_packet_list";

    //红包雨初始化锁
    private static final String RED_PACKET_INIT_LOCK = "red_packet_init_lock";
    private static final String RED_PACKET_TOTAL_GET_COUNT = "red_packet_total_get_count";
    private static final String RED_PACKET_TOTAL_GET_PRICE = "red_packet_total_get_price";
    private static final String RED_PACKET_MAX_GET_PRICE = "red_packet_max_get_price";
    private static final String USER_TOTAL_GET_PRICE_CACHE = "red_packet_user_total_get_price";
    private static final String RED_PACKET_PREPARE_SUCCESS = "red_packet_prepare_success";
    private static final String RED_PACKET_NOTIFY = "red_packet_notify";

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

    public String buildRedPacketList(String code) {
        return super.getPrefix() + RED_PACKET_LIST + super.getSplitItem() + code;
    }

    public String buildRedPacketInitLock(String code) {
        return super.getPrefix() + RED_PACKET_INIT_LOCK + super.getSplitItem() + code;
    }

    public String buildRedPacketTotalGetCount(String code) {
        return super.getPrefix() + RED_PACKET_TOTAL_GET_COUNT + super.getSplitItem() + (Math.abs(code.hashCode()) % 100);
    }

    public String buildRedPacketTotalGetPrice(String code) {
        return super.getPrefix() + RED_PACKET_TOTAL_GET_PRICE + super.getSplitItem() + (Math.abs(code.hashCode()) % 100);
    }

    public String buildRedPacketMaxGetPrice(String code) {
        return super.getPrefix() + RED_PACKET_MAX_GET_PRICE + super.getSplitItem() + (Math.abs(code.hashCode()) % 100);
    }

    public String buildUserTotalGetPrice(Long userId) {
        return super.getPrefix() + USER_TOTAL_GET_PRICE_CACHE + super.getSplitItem() + userId;
    }

    public String buildRedPacketPrepareSuccess(String code) {
        return super.getPrefix() + RED_PACKET_PREPARE_SUCCESS + super.getSplitItem() + code;
    }

    public String buildRedPacketNotify(String code) {
        return super.getPrefix() + RED_PACKET_NOTIFY + super.getSplitItem() + code;
    }

}
