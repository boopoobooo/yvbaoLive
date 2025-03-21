package cn.junbao.yubao.live.api.service;

import cn.junbao.yubao.live.api.vo.req.PayProductReqVO;
import cn.junbao.yubao.live.api.vo.resp.PayProductRespVO;
import cn.junbao.yubao.live.api.vo.resp.PayProductVO;
import jakarta.servlet.http.HttpServletResponse;

public interface IBankService {
    /**
     * 查询相关产品信息
     */
    PayProductVO products(Integer type);

    /**
     * 发起支付
     */
    PayProductRespVO payProduct(PayProductReqVO payProductReqVO);

}
