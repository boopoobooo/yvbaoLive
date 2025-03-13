package cn.junbao.yubao.live.msg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Junbao
 * @Date: 2025/2/19 10:03
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6819144884854677228L;

    private Long userId;
    private Integer roomId;
    //发送人名称
    private String senderName;
    //发送人头像
    private String senderAvtar;
    /**
     * 消息类型
     */
    private Integer type;
    /**
     * 消息内容
     */
    private String content;


    private Date createTime;
    private Date updateTime;


}
