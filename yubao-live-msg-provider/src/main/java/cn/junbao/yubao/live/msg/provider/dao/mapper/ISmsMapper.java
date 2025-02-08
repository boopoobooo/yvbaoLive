package cn.junbao.yubao.live.msg.provider.dao.mapper;

import cn.junbao.yubao.live.msg.provider.dao.po.SmsPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ISmsMapper {

    void insertOne(SmsPO smsPO);
}
