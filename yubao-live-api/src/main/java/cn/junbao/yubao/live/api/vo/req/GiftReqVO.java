package cn.junbao.yubao.live.api.vo.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Junbao
 * @Date: 2025/3/7 23:05
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GiftReqVO {
    private int giftId;
    private Integer roomId;
    private Long senderUserId;
    private Long receiverId;
    private int type;

}
