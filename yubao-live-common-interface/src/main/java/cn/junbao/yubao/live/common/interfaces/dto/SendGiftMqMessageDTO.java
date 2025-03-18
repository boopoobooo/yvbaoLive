package cn.junbao.yubao.live.common.interfaces.dto;

import lombok.Data;

/**
 * @Author: Junbao
 * @Date: 2025/3/7 22:57
 * @Description:
 */
@Data
public class SendGiftMqMessageDTO {
    private Long userId;
    private String senderNickName;//发送礼物者的昵称
    private Integer giftId;
    private String giftName;//礼物名称
    private Integer price;
    private Long receiverId;
    private Integer roomId;
    private String url;
    private String uuid;//业务id值，防重
    private Integer type;

}
