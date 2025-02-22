package cn.junbao.yubao.live.living.provider.service;

import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomReqDTO;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomRespDTO;

public interface ILivingRoomService {
    /**
     * 根据用户id查询是否正在开播
     *
     * @param roomId
     * @return
     */
    LivingRoomRespDTO queryByRoomId(Integer roomId);

    /**
     * 开启直播间
     *
     * @param livingRoomReqDTO
     * @return 直播间id
     */
    Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 关闭直播间
     */
    boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO);
}
