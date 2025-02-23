package cn.junbao.yubao.live.api.service;

import cn.junbao.yubao.live.api.vo.LivingRoomPageRespVO;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomRespDTO;

import java.util.List;

public interface ILivingRoomService {

    Long startLiving(Integer type);

    boolean closeLiving(Long roomId);

    List<LivingRoomRespDTO> list(Integer type, int pageNum, int pageSize );
}
