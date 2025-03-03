package cn.junbao.yubao.im.core.server.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Data
@ToString
public class ImOfflineDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7435114723872010599L;
    private Long userId;
    private Integer appId;
    private Integer roomId;
    private Long logOutTime;

}
