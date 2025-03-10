package cn.junbao.yubao.live.bank.provider.service;

/**
 * @Author idea
 * @Date: Created in 21:16 2023/8/7
 * @Description
 */
public interface ICurrencyTradeService {

    /**
     * 插入一条流水记录
     *
     * @param userId
     * @param num
     * @param type
     * @return
     */
    boolean insertOne(long userId,int num,int type);
}
