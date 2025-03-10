package cn.junbao.yubao.live.api.controller;

import cn.junbao.yubao.live.api.service.IGiftService;
import cn.junbao.yubao.live.api.vo.req.GiftReqVO;
import cn.junbao.yubao.live.api.vo.resp.GiftConfigVO;
import cn.junbao.yubao.live.common.interfaces.vo.WebResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: Junbao
 * @Date: 2025/3/7 22:21
 * @Description:
 */
@RestController
@RequestMapping("/gift")
public class GiftController {
    @Resource
    private IGiftService giftService;

    @PostMapping("/listGift")
    public WebResponseVO listGift() {
        List<GiftConfigVO> giftConfigVOS = giftService.listGift();
        return WebResponseVO.success(giftConfigVOS);
    }

    @PostMapping("/send")
    public WebResponseVO send(GiftReqVO giftReqVO) {
        return WebResponseVO.success(giftService.send(giftReqVO));
    }

}
