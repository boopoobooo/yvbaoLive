package cn.junbao.yubao.live.living.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 直播间相关请求DTO
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LivingRoomRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4370402310595190339L;
    private Integer id;
    private Integer roomId;
    private Long anchorId;
    private String roomName;
    private String covertImg;
    private Integer type;
    private Integer watchNum;
    private Integer goodNum;
    private Long pkObjId;

}
