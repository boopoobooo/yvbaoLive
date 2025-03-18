package cn.junbao.yubao.live.gift.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Junbao
 * @Date: 2025/3/17 19:00
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendGiftRespMsgDTO {
    private String svgaUrl ;
    private String senderNickName;
    private String giftName;

}
