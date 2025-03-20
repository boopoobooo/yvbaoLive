package cn.junbao.yubao.live.gift.bo;

import cn.junbao.yubao.live.gift.dto.RedPacketConfigReqDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: Junbao
 * @Date: 2025/3/18 17:59
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendRedPacketBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4143825749513407846L;
    private Integer price;//抢到的金额
    private RedPacketConfigReqDTO reqDTO;

}
