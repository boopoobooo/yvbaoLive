package cn.junbao.yubao.live.living.provider.dao.mapper;

import cn.junbao.yubao.live.living.provider.dao.po.LivingRoomRecordPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ILivingRoomRecordMapper {

    void insertOne(LivingRoomRecordPO livingRoomRecordPO);

}
