package cn.junbao.yubao.live.living.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 直播间相关请求DTO
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LivingRoomReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4370401310595190339L;
    private Integer id;
    private Long anchorId; //主播id
    private Long pkObjId;//pk 对象id
    private String roomName;
    private Integer roomId;
    private String covertImg;
    private Integer type;
    private Integer appId;
    private int page;
    private int pageSize;

}
