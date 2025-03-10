package cn.junbao.yubao.live.bank.provider.rpc;

import cn.junbao.yubao.live.bank.dto.AccountTradeReqDTO;
import cn.junbao.yubao.live.bank.dto.AccountTradeRespDTO;
import cn.junbao.yubao.live.bank.interfaces.ICurrencyAccountRpc;
import cn.junbao.yubao.live.bank.provider.service.ICurrencyAccountService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

/**
 */
@DubboService
public class CurrencyAccountRpcImpl implements ICurrencyAccountRpc {

    @Resource
    private ICurrencyAccountService currencyAccountService;

    @Override
    public boolean insertOne(Long userId) {
        return currencyAccountService.insertOne(userId);
    }

    @Override
    public void incr(long userId, int num) {
        currencyAccountService.incr(userId, num);
    }

    @Override
    public void decr(long userId, int num) {
        currencyAccountService.decr(userId, num);
    }

    @Override
    public Integer getBalance(long userId) {
        return currencyAccountService.getBalance(userId);
    }

    @Override
    public AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO) {
        return currencyAccountService.consumeForSendGift(accountTradeReqDTO);
    }

}
