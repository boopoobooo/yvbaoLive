package cn.junbao.yubao.live.api.service;

import cn.junbao.yubao.live.api.vo.LivingRoomInitVO;
import cn.junbao.yubao.live.api.vo.LivingRoomPageRespVO;
import cn.junbao.yubao.live.api.vo.req.OnlinePKReqVO;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomRespDTO;

import java.util.List;

public interface ILivingRoomService {

    Integer startLiving(Integer type);

    boolean closeLiving(Integer roomId);

    List<LivingRoomRespDTO> list(Integer type, int pageNum, int pageSize );

    LivingRoomInitVO anchorConfig(Long userId, Integer roomId);

    /**
     * 当PK直播间连上线准备PK时，调用该请求
     */
    boolean onlinePK(OnlinePKReqVO onlinePKReqVO);
}
