package cn.junbao.yubao.im.core.server.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Data
@ToString
public class ImOnlineDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5699198397406127124L;
    private Long userId;
    private Integer appId;
    private Integer roomId;
    private Long loginTime;

}
