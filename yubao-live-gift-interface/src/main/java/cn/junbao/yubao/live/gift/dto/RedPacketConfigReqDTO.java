package cn.junbao.yubao.live.gift.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: Junbao
 * @Date: 2025/3/18 17:16
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedPacketConfigReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4025676027827098989L;
    private Integer id;
    private Integer roomId;
    private Integer status;
    private Long userId;
    private String redPacketConfigCode;
    private Integer totalPrice;
    private Integer totalCount;
    private String remark;
}
