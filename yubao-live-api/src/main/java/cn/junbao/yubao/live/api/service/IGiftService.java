package cn.junbao.yubao.live.api.service;

import cn.junbao.yubao.live.api.vo.req.GiftReqVO;
import cn.junbao.yubao.live.api.vo.resp.GiftConfigVO;

import java.util.List;

public interface IGiftService {
    List<GiftConfigVO> listGift();

    boolean send(GiftReqVO giftReqVO);
}
