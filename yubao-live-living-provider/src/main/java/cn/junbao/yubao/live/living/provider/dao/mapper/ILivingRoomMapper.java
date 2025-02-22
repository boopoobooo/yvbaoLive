package cn.junbao.yubao.live.living.provider.dao.mapper;

import cn.junbao.yubao.live.living.provider.dao.po.LivingRoomPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ILivingRoomMapper {

    void insertOne(LivingRoomPO livingRoomPO);

    LivingRoomPO selectByRoomId(@Param("roomId") Integer roomId);

    void deleteByRoomId(Integer roomId);
}
