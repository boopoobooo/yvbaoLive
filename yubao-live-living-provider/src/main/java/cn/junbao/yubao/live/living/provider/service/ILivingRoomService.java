package cn.junbao.yubao.live.living.provider.service;

import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomReqDTO;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomRespDTO;
import cn.junbao.yubao.live.living.provider.dao.po.LivingRoomPO;

import java.util.List;

public interface ILivingRoomService {
    /**
     * 根据用户id查询是否正在开播
     *
     * @param roomId
     * @return
     */
    LivingRoomRespDTO queryByRoomId(Long roomId);

    /**
     * 开启直播间
     *
     * @param livingRoomReqDTO
     * @return 直播间id
     */
    Long startLivingRoom(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 关闭直播间
     */
    boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO);

    List<LivingRoomRespDTO> getLivingRoomPage(Integer type , int pageNum, int pageSize);

    List<LivingRoomRespDTO> listAllLivingRoomFromDB(Integer type);
}
