package cn.junbao.yubao.live.bank.provider.dao.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 09:41
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayOrderPO {
    private Long id;
    private Long orderId;
    private Integer productId;
    private String productName;
    private String productDesc;
    private Integer totalAmount;
    private Long userId;
    private Integer source;
    private Integer payChannel;
    private Integer status;
    private Date payTime;
    private Date createTime;
    private Date updateTime;

}
