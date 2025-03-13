package cn.junbao.yubao.live.bank.provider.service;

import cn.junbao.yubao.live.bank.dto.PayProductDTO;

import java.util.List;

public interface IPayProductService {

    /**
     * 根据产品类型，返回批量的商品信息
     */
    List<PayProductDTO> products(Integer type);
}
