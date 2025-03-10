package cn.junbao.yubao.live.bank.provider.service;

import cn.junbao.yubao.live.bank.dto.AccountTradeReqDTO;
import cn.junbao.yubao.live.bank.dto.AccountTradeRespDTO;

public interface ICurrencyAccountService {

    /**
     * 新增账户
     *
     * @param userId
     */
    boolean insertOne(long userId);

    /**
     * 增加虚拟币
     *
     * @param userId
     * @param num
     */
    void incr(long userId,int num);

    /**
     * 扣减虚拟币
     *
     * @param userId
     * @param num
     */
    void decr(long userId,int num);

    /**
     * 查询余额
     *
     * @param userId
     * @return
     */
    Integer getBalance(long userId);

    AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO);

}
