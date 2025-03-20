package cn.junbao.yubao.live.gift.provider.rpc;

import cn.junbao.yubao.live.common.interfaces.utils.ConvertBeanUtils;
import cn.junbao.yubao.live.gift.dto.RedPacketConfigReqDTO;
import cn.junbao.yubao.live.gift.dto.RedPacketConfigRespDTO;
import cn.junbao.yubao.live.gift.dto.RedPacketReceiveDTO;
import cn.junbao.yubao.live.gift.interfaces.IRedPacketConfigRpc;
import cn.junbao.yubao.live.gift.provider.dao.po.RedPacketConfigPO;
import cn.junbao.yubao.live.gift.provider.service.IRedPacketConfigService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author: Junbao
 * @Date: 2025/3/18 17:22
 * @Description:
 */
@DubboService
public class RedPacketConfigRpcImpl implements IRedPacketConfigRpc {
    @Resource
    private IRedPacketConfigService redPacketConfigService;

    @Override
    public RedPacketConfigRespDTO queryByAnchorId(Long anchorId) {
        return ConvertBeanUtils.convert(redPacketConfigService.queryByAnchorId(anchorId), RedPacketConfigRespDTO.class);
    }

    @Override
    public boolean addOne(RedPacketConfigReqDTO redPacketConfigReqDTO) {
        return redPacketConfigService.addOne(ConvertBeanUtils.convert(redPacketConfigReqDTO, RedPacketConfigPO.class));
    }

    @Override
    public boolean prepareRedPacket(Long anchorId) {
        return redPacketConfigService.prepareRedPacket(anchorId);
    }

    @Override
    public RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO redPacketConfigReqDTO) {
        return redPacketConfigService.receiveRedPacket(redPacketConfigReqDTO);
    }

    @Override
    public Boolean startRedPacket(RedPacketConfigReqDTO reqDTO) {
        return redPacketConfigService.startRedPacket(reqDTO);
    }

}
