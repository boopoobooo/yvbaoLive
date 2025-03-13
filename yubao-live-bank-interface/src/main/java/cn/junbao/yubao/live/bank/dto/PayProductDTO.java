package cn.junbao.yubao.live.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Junbao
 * @Date: 2025/3/10 20:17
 * @Description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PayProductDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6759429675227811339L;
    private Long id;
    private String name;
    private Integer price;
    private String extra;
    private Integer type;
    private Integer validStatus;
    private Date createTime;
    private Date updateTime;

}
