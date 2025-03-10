package cn.junbao.yubao.live.gift.provider.dao.mapper;


import cn.junbao.yubao.live.gift.provider.dao.po.GiftRecordPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IGiftRecordMapper {
    int insertOne(GiftRecordPO giftRecordPO);
}
