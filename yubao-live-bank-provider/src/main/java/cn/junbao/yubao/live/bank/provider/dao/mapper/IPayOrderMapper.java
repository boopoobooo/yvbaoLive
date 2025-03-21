package cn.junbao.yubao.live.bank.provider.dao.mapper;

import cn.junbao.yubao.live.bank.provider.dao.po.PayOrderPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 09:44
 * @Description:
 */
@Mapper
public interface IPayOrderMapper {


    PayOrderPO selectByProductId(@Param("id") Long id);

    PayOrderPO selectOne(PayOrderPO payOrderPO);

    int insertOne(PayOrderPO payOrderPO);

    int updateById(PayOrderPO payOrderPO);

    int updateByOrderId(PayOrderPO payOrderPO);

    List<Long> queryNoPayNotifyOrder();

    List<Long> queryTimeoutCloseOrderList();

}
