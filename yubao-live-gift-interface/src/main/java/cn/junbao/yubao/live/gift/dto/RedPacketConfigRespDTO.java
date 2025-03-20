package cn.junbao.yubao.live.gift.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: Junbao
 * @Description: 红包雨配置的响应dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedPacketConfigRespDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4566036964655707749L;
    private Long anchorId;
    private Integer totalPrice;
    private Integer totalCount;
    private String configCode;
    private String remark;

}
