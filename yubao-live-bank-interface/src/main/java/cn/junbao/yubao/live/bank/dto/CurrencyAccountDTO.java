package cn.junbao.yubao.live.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Junbao
 * @Date: 2025/3/7 22:26
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyAccountDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2014440317620410635L;
    private Long userId;
    private int currentBalance;
    private int totalCharged;
    private Integer status;
    private Date createTime;
    private Date updateTime;

}
