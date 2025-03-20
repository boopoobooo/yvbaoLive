package cn.junbao.yubao.live.gift.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: Junbao
 * @Date: 2025/3/18 17:21
 * @Description:
 */
@Data
@AllArgsConstructor
public class RedPacketReceiveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 797507475126740728L;

    private Integer price;
    private String notifyMsg;
}
