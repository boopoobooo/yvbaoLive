package cn.junbao.yubao.live.api.service;

import cn.junbao.yubao.live.api.vo.resp.PayProductVO;

public interface IBankService {
    /**
     * 查询相关产品信息
     */
    PayProductVO products(Integer type);
}
