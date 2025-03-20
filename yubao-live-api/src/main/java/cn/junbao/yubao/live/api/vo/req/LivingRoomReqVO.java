package cn.junbao.yubao.live.api.vo.req;

import lombok.Data;

/**
 * @Author: Junbao
 * @Date: 2025/3/18 20:00
 * @Description:
 */
@Data
public class LivingRoomReqVO {

    private Integer type;
    private int pageNum;
    private int pageSize;
    private Integer roomId;
    private String redPacketConfigCode;
}
