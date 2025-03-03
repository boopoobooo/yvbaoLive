package cn.junbao.yubao.live.living.provider.service;

import cn.junbao.yubao.im.core.server.dto.ImOfflineDTO;
import cn.junbao.yubao.im.core.server.dto.ImOnlineDTO;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomReqDTO;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomRespDTO;
import cn.junbao.yubao.live.living.provider.dao.po.LivingRoomPO;

import java.util.List;

public interface ILivingRoomService {

    /**
     * 支持根据roomId查询出批量的userId（set）存储，3000个人，元素非常多，O(n)
     *
     * @param livingRoomReqDTO
     * @return
     */
    List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 用户下线处理
     *
     * @param imOfflineDTO
     */
    void userOfflineHandler(ImOfflineDTO imOfflineDTO);

    /**
     * 用户上线处理
     *
     * @param imOnlineDTO
     */
    void userOnlineHandler(ImOnlineDTO imOnlineDTO);


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

    List<LivingRoomRespDTO> getLivingRoomPage(Integer type , int pageNum, int pageSize);

    List<LivingRoomRespDTO> listAllLivingRoomFromDB(Integer type);
}
