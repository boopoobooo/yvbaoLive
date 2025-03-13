package cn.junbao.yubao.live.bank.provider.dao.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Junbao
 * @Date: 2025/3/10 20:11
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayProductPO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7397701943457229766L;
    private Long id;
    private String name;
    private Integer price;
    private String extra;
    private Integer type;
    private Integer validStatus;
    private Date createTime;
    private Date updateTime;

}
