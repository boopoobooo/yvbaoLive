package cn.junbao.yubao.live.im.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImMsgBody implements Serializable {

    @Serial
    private static final long serialVersionUID = -7657602083071950966L;

    /**
     * 接入Im服务的是各个业务线id
     */
    private int appId;
    /**
     * 当前消息的唯一id
     */
    private String msgId;
    /**
     * 消息的校验token
     */
    private String token;
    /**
     * 当前消息对应的用户id
     */
    private Long userId;
    /**
     * 向下游传递的消息体
     */
    private String data;

}
