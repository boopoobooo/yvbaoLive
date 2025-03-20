package cn.junbao.yubao.live.gift.provider.dao.mapper;

import cn.junbao.yubao.live.gift.provider.dao.po.RedPacketConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface IRedPacketConfigMapper {
    List<RedPacketConfigPO> findByAnchorId(@Param("anchorId") Long anchorId);

    int updateRedPacketConfig(RedPacketConfigPO redPacketConfigPO);

    RedPacketConfigPO selectOne(RedPacketConfigPO redPacketConfigReqPO);

    int insert(RedPacketConfigPO redPacketConfigPO);

    int updateById(RedPacketConfigPO redPacketConfigPO);
}
