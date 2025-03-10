package cn.junbao.yubao.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Junbao
 * @Date: 2025/3/7 22:28
 * @Description:
 */
@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class BankProviderCacheKeyBuilder extends RedisKeyBuilder  {
    private static final String BALANCE_CACHE = "balance_cache";

    private static final String PAY_PRODUCT_CACHE = "pay_product_cache";

    private static final String PAY_PRODUCT_ITEM_CACHE = "pay_product_item_cache";

    //账户的操作锁
    private static final String BALANCE_LOCK = "balance:lock:";

    public String buildBalanceLockKey(Long userId) {
        return super.getPrefix() + BALANCE_LOCK + super.getSplitItem() + userId;
    }

    public String buildPayProductItemCache(Integer productId) {
        return super.getPrefix() + PAY_PRODUCT_ITEM_CACHE + super.getSplitItem() + productId;
    }

    /**
     * 按照产品的类型来进行检索
     */
    public String buildPayProductCache(Integer type) {
        return super.getPrefix() + PAY_PRODUCT_CACHE + super.getSplitItem() + type;
    }

    /**
     * 构建用户余额cache key
     */
    public String buildUserBalance(Long userId) {
        return super.getPrefix() + BALANCE_CACHE + super.getSplitItem() + userId;
    }

}
