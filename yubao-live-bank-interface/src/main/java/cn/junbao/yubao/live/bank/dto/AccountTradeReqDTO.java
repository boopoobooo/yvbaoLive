package cn.junbao.yubao.live.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: Junbao
 * @Date: 2025/3/7 23:38
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountTradeReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -7944354842421998990L;
    private Long userId;
    private Integer num;

}
