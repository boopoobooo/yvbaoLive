package cn.junbao.yubao.live.living.interfaces.rpc;



import cn.junbao.yubao.live.living.interfaces.dto.LivingPkRespDTO;
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

    List<LivingRoomRespDTO> list(Integer type,int pageNum, int pageSize );

    List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 当PK直播间连上线准备PK时，调用该请求
     */
    LivingPkRespDTO onlinePK(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 用户在pk直播间下线
     *
     * @param livingRoomReqDTO
     * @return
     */
    boolean offlinePk(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 根据roomId查询当前pk人是谁
     */
    Long queryOnlinePkUserId(Integer roomId);
}
