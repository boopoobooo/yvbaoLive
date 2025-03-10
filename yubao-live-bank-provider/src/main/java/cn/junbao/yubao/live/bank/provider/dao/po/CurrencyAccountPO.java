package cn.junbao.yubao.live.bank.provider.dao.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 平台的虚拟币账户
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyAccountPO {
    private Long id;
    private Long userId;
    private int currentBalance;
    private int totalCharged;
    private Integer status;
    private Date createTime;
    private Date updateTime;

}
