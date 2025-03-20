package cn.junbao.yubao.live.gift.provider.service;

import cn.junbao.yubao.live.gift.dto.RedPacketConfigReqDTO;
import cn.junbao.yubao.live.gift.dto.RedPacketReceiveDTO;
import cn.junbao.yubao.live.gift.provider.dao.po.RedPacketConfigPO;

public interface IRedPacketConfigService {
    /**
     * 根据主播id查询有无发放红包雨的特权
     */
    RedPacketConfigPO queryByAnchorId(Long anchorId);

    /**
     * 根据code查询已准备的红包雨配置信息
     */
    RedPacketConfigPO queryByConfigCode(String code);

    /**
     * 新增红包雨配置
     */
    boolean addOne(RedPacketConfigPO redPacketConfigPO);

    /**
     * 更新红包雨配置
     */
    boolean updateById(RedPacketConfigPO redPacketConfigPO);

    /**
     * 主播开始准备红包雨
     */
    boolean prepareRedPacket(Long anchorId);

    /**
     * 直播间用户领取红包
     */
    RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO redPacketConfigReqDTO);

    /**
     * 开始红包雨
     */
    Boolean startRedPacket(RedPacketConfigReqDTO reqDTO);

    /**
     * 处理用户抢到红包后的相关消息处理
     * @param reqDTO
     * @param price
     */
    void receiveRedPacketHandler(RedPacketConfigReqDTO reqDTO, Integer price);
}
