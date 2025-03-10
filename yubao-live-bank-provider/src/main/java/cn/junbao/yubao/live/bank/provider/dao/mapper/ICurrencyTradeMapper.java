package cn.junbao.yubao.live.bank.provider.dao.mapper;


import cn.junbao.yubao.live.bank.provider.dao.po.CurrencyTradePO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ICurrencyTradeMapper  {
    void insertOne(CurrencyTradePO currencyTradePO);

    CurrencyTradePO selectById(@Param("id") Long id);

    List<CurrencyTradePO> selectAll();
}
