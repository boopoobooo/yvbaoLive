package cn.junbao.yubao.live.api.vo;

import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomRespDTO;
import lombok.Data;

import java.util.List;

/**
 * @Author: Junbao
 * @Date: 2025/2/23 09:37
 * @Description:
 */
@Data
public class LivingRoomPageRespVO {
    private List<LivingRoomRespDTO> list;
    private boolean hasNext;

}
