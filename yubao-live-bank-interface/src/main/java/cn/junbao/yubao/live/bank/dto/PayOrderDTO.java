package cn.junbao.yubao.live.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 09:41
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayOrderDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7571445890267627658L;
    private Long id;
    private Long orderId;
    private Integer productId;
    private String productName;
    private String productDesc;
    private Integer totalAmount;
    private Integer bizCode;
    private Long userId;
    private Integer source;
    private Integer payChannel;
    private Integer status;
    private Date payTime;

    private String payUrl;
}
