package cn.junbao.yubao.live.api.service.impl;

import cn.junbao.yubao.live.api.service.ILivingRoomService;
import cn.junbao.yubao.live.framework.web.strater.context.WebRequestContext;
import cn.junbao.yubao.live.im.constants.AppIdEnum;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomReqDTO;
import cn.junbao.yubao.live.living.interfaces.enums.LivingRoomType;
import cn.junbao.yubao.live.living.interfaces.rpc.ILivingRoomRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @Author: Junbao
 * @Date: 2025/2/22 23:55
 * @Description:
 */
@Service
public class LivingRoomServiceImpl implements ILivingRoomService {
    @DubboReference
    private ILivingRoomRpc iLivingRoomRpc;


    /**
     * 直播间开播
     */
    @Override
    public Integer startLiving(Integer type) {
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setAnchorId(WebRequestContext.getUserId());
        livingRoomReqDTO.setRoomName("主播:"+WebRequestContext.getUserId()+"的房间");
        livingRoomReqDTO.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
        livingRoomReqDTO.setType(type);
        Integer livingRoomId = iLivingRoomRpc.startLivingRoom(livingRoomReqDTO);

        return livingRoomId;
    }

    @Override
    public boolean closeLiving(Integer roomId) {
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setAnchorId(WebRequestContext.getUserId());
        livingRoomReqDTO.setRoomId(roomId);
        return iLivingRoomRpc.closeLiving(livingRoomReqDTO );
    }
}
