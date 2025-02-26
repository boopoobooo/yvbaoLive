package cn.junbao.yubao.live.api.vo.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * @Author: Junbao
 * @Date: 2025/2/25 15:24
 * @Description:
 */
@Data
@AllArgsConstructor
@ToString
public class UserPhoneLoginReqVO {
    private String phone;
    private String code;

}
