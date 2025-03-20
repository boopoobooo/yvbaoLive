package cn.junbao.yubao.live.gift.provider.dao.po;

import lombok.Data;

import java.util.Date;

/**
 * @Author: Junbao
 * @Date: 2025/3/18 17:14
 * @Description:
 */
@Data
public class RedPacketConfigPO {
    private Integer id;
    private Long anchorId;
    private Date startTime;
    private Integer totalGet;
    private Integer totalGetPrice;
    private Integer maxGetPrice;
    private Integer status;
    private Integer totalPrice;
    private Integer totalCount;
    private String configCode;
    private String remark;
    private Date createTime;
    private Date updateTime;

}
