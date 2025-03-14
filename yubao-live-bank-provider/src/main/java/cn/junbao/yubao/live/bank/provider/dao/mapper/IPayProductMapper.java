package cn.junbao.yubao.live.bank.provider.dao.mapper;

import cn.junbao.yubao.live.bank.provider.dao.po.PayProductPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IPayProductMapper {

    PayProductPO selectById(Long id );

    PayProductPO selectByProductId(Integer productId);

    int insertOne(PayProductPO payProductPO);

    List<PayProductPO> selectListByType(@Param("type") Integer type, @Param("validStatus") Integer validStatus);

}
