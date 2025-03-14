package cn.junbao.yubao.live.api.vo.req;

import lombok.Data;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 10:08
 * @Description:
 */
@Data
public class PayProductReqVO {
    // 产品id
    private Integer productId;
    /**
     * 支付来源（直播间内，用户中心），用于统计支付页面来源
     * @see cn.junbao.yubao.live.bank.constants.PaySourceEnum
     */
    private Integer paySource;
    /**
     * 支付渠道
     * @see cn.junbao.yubao.live.bank.constants.PayChannelEnum
     */
    private Integer payChannel;

}
