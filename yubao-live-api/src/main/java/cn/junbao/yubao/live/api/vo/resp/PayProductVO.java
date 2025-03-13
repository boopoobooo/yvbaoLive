package cn.junbao.yubao.live.api.vo.resp;

import lombok.Data;

import java.util.List;

/**
 * @Author: Junbao
 * @Date: 2025/3/10 20:26
 * @Description:
 */
@Data
public class PayProductVO {
    /**
     * 当前余额
     */
    private Integer currentBalance;
    /**
     * 一系列付费产品
     */
    private List<PayProductItemVO> payProductItemVOList;

}
