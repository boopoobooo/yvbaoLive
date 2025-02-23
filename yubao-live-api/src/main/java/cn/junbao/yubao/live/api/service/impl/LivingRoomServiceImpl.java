package cn.junbao.yubao.live.api.service.impl;

import cn.junbao.yubao.live.api.service.ILivingRoomService;
import cn.junbao.yubao.live.api.vo.LivingRoomPageRespVO;
import cn.junbao.yubao.live.framework.web.strater.context.WebRequestContext;
import cn.junbao.yubao.live.im.constants.AppIdEnum;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomReqDTO;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomRespDTO;
import cn.junbao.yubao.live.living.interfaces.rpc.ILivingRoomRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Junbao
 * @Date: 2025/2/22 23:55
 * @Description:
 */
@Service
public class LivingRoomServiceImpl implements ILivingRoomService {
    @DubboReference
    private ILivingRoomRpc livingRoomRpc;


    /**
     * 直播间开播
     */
    @Override
    public Long startLiving(Integer type) {
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setAnchorId(WebRequestContext.getUserId());
        livingRoomReqDTO.setRoomName("主播:"+WebRequestContext.getUserId()+"的房间");
        livingRoomReqDTO.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
        livingRoomReqDTO.setType(type);
        Long livingRoomId = livingRoomRpc.startLivingRoom(livingRoomReqDTO);

        return livingRoomId;
    }

    @Override
    public boolean closeLiving(Long roomId) {
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setAnchorId(WebRequestContext.getUserId());
        livingRoomReqDTO.setRoomId(roomId);
        return livingRoomRpc.closeLiving(livingRoomReqDTO );
    }

    @Override
    public List<LivingRoomRespDTO> list(Integer type,int pageNum, int pageSize) {
        List<LivingRoomRespDTO> livingRoomRespDTOList = livingRoomRpc.list(type,pageNum,pageSize);
        return livingRoomRespDTOList;
    }
}
