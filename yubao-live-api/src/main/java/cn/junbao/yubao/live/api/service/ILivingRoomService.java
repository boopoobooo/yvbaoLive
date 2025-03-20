package cn.junbao.yubao.live.api.service;

import cn.junbao.yubao.live.api.vo.LivingRoomInitVO;
import cn.junbao.yubao.live.api.vo.req.LivingRoomReqVO;
import cn.junbao.yubao.live.api.vo.req.OnlinePKReqVO;
import cn.junbao.yubao.live.api.vo.resp.RedPacketReceiveRespVO;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomRespDTO;

import java.util.List;

public interface ILivingRoomService {

    Integer startLiving(Integer type);

    boolean closeLiving(Integer roomId);

    List<LivingRoomRespDTO> list(LivingRoomReqVO livingRoomReqVO);

    LivingRoomInitVO anchorConfig(Long userId, Integer roomId);

    /**
     * 当PK直播间连上线准备PK时，调用该请求
     */
    boolean onlinePK(OnlinePKReqVO onlinePKReqVO);


    /**
     * 主播点击开始准备红包雨金额
     */
    Boolean prepareRedPacket(Long userId, Integer roomId);

    /**
     * 主播开始红包雨
     */
    Boolean startRedPacket(Long userId, String code);

    /**
     * 根据红包雨code领取红包
     */
    RedPacketReceiveRespVO getRedPacket(Long userId, String redPacketConfigCode);
}
