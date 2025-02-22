package cn.junbao.yubao.live.living.interfaces.rpc;



import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomReqDTO;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomRespDTO;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 21:20 2023/7/19
 * @Description
 */
public interface ILivingRoomRpc {


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
