package cn.junbao.yubao.live.msg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: Junbao
 * @Date: 2025/2/19 10:03
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    //接收人id
    private Long userId;
    //发送人名称
    private String senderName;
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
