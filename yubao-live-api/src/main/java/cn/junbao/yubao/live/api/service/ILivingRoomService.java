package cn.junbao.yubao.live.api.service;

import cn.junbao.yubao.live.api.vo.LivingRoomPageRespVO;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomRespDTO;

import java.util.List;

public interface ILivingRoomService {

    Integer startLiving(Integer type);

    boolean closeLiving(Integer roomId);

    List<LivingRoomRespDTO> list(Integer type, int pageNum, int pageSize );
}
