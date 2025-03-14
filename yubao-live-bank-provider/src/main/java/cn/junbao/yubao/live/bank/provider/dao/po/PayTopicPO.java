package cn.junbao.yubao.live.bank.provider.dao.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 15:03
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayTopicPO {
    private Long id;
    private String topic;
    private Integer status;
    private Integer bizCode;
    private String remark;
    private Date createTime;
    private Date updateTime;

}
