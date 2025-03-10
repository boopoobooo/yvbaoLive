package cn.junbao.yubao.live.bank.provider.dao.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 旗鱼平台的虚拟币账户
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyTradePO {

    private Long id;
    private Long userId;
    private Integer num;
    private Integer type;
    private Integer status;
    private Date createTime;
    private Date updateTime;

}
