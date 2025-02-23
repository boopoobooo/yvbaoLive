package cn.junbao.yubao.live.living.provider.rpc;

import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomReqDTO;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomRespDTO;
import cn.junbao.yubao.live.living.interfaces.rpc.ILivingRoomRpc;
import cn.junbao.yubao.live.living.provider.service.ILivingRoomService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * @Author: Junbao
 * @Date: 2025/2/22 23:29
 * @Description:
 */
@DubboService
public class LivingRoomRpcImpl implements ILivingRoomRpc {

    @Resource
    private ILivingRoomService livingRoomService;

    @Override
    public LivingRoomRespDTO queryByRoomId(Long roomId) {
        return livingRoomService.queryByRoomId(roomId);
    }

    @Override
    public Long startLivingRoom(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.startLivingRoom(livingRoomReqDTO);
    }

    @Override
    public boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.closeLiving(livingRoomReqDTO);
    }

    @Override
    public List<LivingRoomRespDTO> list(Integer type, int pageNum, int pageSize) {
        return livingRoomService.getLivingRoomPage(type,pageNum,pageSize);
    }
}
