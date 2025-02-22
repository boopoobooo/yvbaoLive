package cn.junbao.yubao.live.api.controller;

import cn.junbao.yubao.live.api.service.ILivingRoomService;
import cn.junbao.yubao.live.api.vo.LivingRoomInitVO;
import cn.junbao.yubao.live.common.interfaces.vo.WebResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Junbao
 * @Date: 2025/2/22 23:54
 * @Description:
 */
@RestController
@RequestMapping("/living")
public class LivingRoomController {

    @Resource
    private ILivingRoomService livingRoomService;

    @PostMapping("/startingLiving")
    public WebResponseVO startingLiving(Integer type) {
        Integer roomId = livingRoomService.startLiving(type);
        LivingRoomInitVO initVO = new LivingRoomInitVO();
        initVO.setRoomId(roomId);
        return WebResponseVO.success(initVO);
    }


    @PostMapping("/closeLiving")
    public WebResponseVO closeLiving(Integer roomId) {
        boolean closeStatus = livingRoomService.closeLiving(roomId);
        if (closeStatus) {
            return WebResponseVO.success();
        }
        return WebResponseVO.bizError("关播异常");
    }
}
