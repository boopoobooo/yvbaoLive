package cn.junbao.yubao.live.bank.provider.dao.mapper;

import cn.junbao.yubao.live.bank.provider.dao.po.CurrencyAccountPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 虚拟币账户mapper
 *
 */
@Mapper
public interface ICurrencyAccountMapper  {

    int insertOne(CurrencyAccountPO currencyAccountPO);

    void incr(@Param("userId") long userId,@Param("num") int num);

    Integer queryBalance(@Param("userId") long userId);

    void decr(@Param("userId") long userId,@Param("num") int num);


}
