package cn.junbao.yubao.live.living.interfaces.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author: Junbao
 * @Date: 2025/3/15 17:33
 * @Description:
 */
@Data
public class LivingPkRespDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -5745215600858359818L;
    private boolean onlineStatus;
    private String msg;

}
