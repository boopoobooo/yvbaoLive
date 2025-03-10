package cn.junbao.yubao.live.gift.provider.dao.mapper;


import cn.junbao.yubao.live.gift.provider.dao.po.GiftConfigPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 15:04 2023/7/30
 * @Description
 */
@Mapper
public interface IGiftConfigMapper {
    GiftConfigPO getGiftConfigById(GiftConfigPO giftConfigPO);

    int insertOne(GiftConfigPO giftConfigPO);

    List<GiftConfigPO> selectList();


    int updateOne(GiftConfigPO giftConfigPO);
}
