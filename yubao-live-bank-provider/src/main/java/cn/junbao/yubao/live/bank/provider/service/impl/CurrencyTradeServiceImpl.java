package cn.junbao.yubao.live.bank.provider.service.impl;

import cn.junbao.yubao.live.bank.provider.dao.mapper.ICurrencyTradeMapper;
import cn.junbao.yubao.live.bank.provider.dao.po.CurrencyTradePO;
import cn.junbao.yubao.live.bank.provider.service.ICurrencyTradeService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CurrencyTradeServiceImpl implements ICurrencyTradeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyTradeServiceImpl.class);

    @Resource
    private ICurrencyTradeMapper currencyTradeMapper;

    @Override
    public boolean insertOne(long userId, int num, int type) {
        try {
            CurrencyTradePO tradePO = new CurrencyTradePO();
            tradePO.setUserId(userId);
            tradePO.setNum(num);
            tradePO.setType(type);
            currencyTradeMapper.insertOne(tradePO);
            return true;
        } catch (Exception e) {
            LOGGER.error("[CurrencyTradeServiceImpl] insert error is:", e);
        }
        return false;
    }
}
