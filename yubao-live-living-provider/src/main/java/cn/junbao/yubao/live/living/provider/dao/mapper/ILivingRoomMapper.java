package cn.junbao.yubao.live.living.provider.dao.mapper;

import cn.junbao.yubao.live.living.provider.dao.po.LivingRoomPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ILivingRoomMapper {

    void insertOne(LivingRoomPO livingRoomPO);

    LivingRoomPO selectByRoomId(@Param("roomId") Integer roomId);

    void deleteByRoomId(Integer roomId);

    List<LivingRoomPO> selectLivingRoomPage(@Param("type") Integer type,@Param("offset") int offset, @Param("limit") int limit);

    List<LivingRoomPO>  selectAll(@Param("type") Integer type);

    LivingRoomPO selectOneByRoomId(@Param("roomId") Integer roomId);
}
