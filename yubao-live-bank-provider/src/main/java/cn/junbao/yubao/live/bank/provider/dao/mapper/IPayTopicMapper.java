package cn.junbao.yubao.live.bank.provider.dao.mapper;

import cn.junbao.yubao.live.bank.provider.dao.po.PayTopicPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 15:04
 * @Description:
 */
@Mapper
public interface IPayTopicMapper {

    PayTopicPO selectOne(PayTopicPO payTopicPO);

    int insertOne(PayTopicPO payTopicPO);

    int update(PayTopicPO payTopicPO);

    int deleteById(@Param("id") Long id);

}
