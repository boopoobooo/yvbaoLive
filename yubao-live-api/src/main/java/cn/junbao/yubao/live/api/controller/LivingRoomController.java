package cn.junbao.yubao.live.api.controller;

import cn.junbao.yubao.live.api.service.ILivingRoomService;
import cn.junbao.yubao.live.api.vo.LivingRoomInitVO;
import cn.junbao.yubao.live.api.vo.req.OnlinePKReqVO;
import cn.junbao.yubao.live.common.interfaces.vo.WebResponseVO;
import cn.junbao.yubao.live.framework.web.strater.context.WebRequestContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class LivingRoomController {

    @Resource
    private ILivingRoomService livingRoomService;

    @PostMapping("/list")
    public WebResponseVO list(Integer type,int pageNum, int pageSize){
        if (type == null ){
            log.warn("[list]参数异常，type is null");
            return WebResponseVO.errorParam("type is null");
        }
        return WebResponseVO.success(livingRoomService.list(type,pageNum,pageSize));
    }


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

    /**
     * 获取主播相关配置信息（只有主播才会有权限）
     *
     * @return
     */
    @PostMapping("/anchorConfig")
    public WebResponseVO anchorConfig(Integer roomId) {
        log.info("[anchorConfig] roomId = {}",roomId);
        return WebResponseVO.success(livingRoomService.anchorConfig(WebRequestContext.getUserId(), roomId));
    }

    //LivingRoomController
    @PostMapping("/onlinePK")
    public WebResponseVO onlinePk(OnlinePKReqVO onlinePKReqVO) {
        if (onlinePKReqVO == null ){
            log.warn("[onlinePk] onlinePKReqVO is null");
            return WebResponseVO.errorParam();
        }
        return WebResponseVO.success(livingRoomService.onlinePK(onlinePKReqVO));
    }
}
