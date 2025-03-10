package cn.junbao.yubao.live.gift.provider.rpc;

import cn.junbao.yubao.live.gift.dto.GiftRecordDTO;
import cn.junbao.yubao.live.gift.interfaces.IGiftRecordRpc;
import cn.junbao.yubao.live.gift.provider.service.IGiftRecordService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author idea
 * @Date: Created in 15:10 2023/7/30
 * @Description
 */
@DubboService
public class GiftRecordRpcImpl implements IGiftRecordRpc {

    @Resource
    private IGiftRecordService giftRecordService;

    @Override
    public void insertOne(GiftRecordDTO giftRecordDTO) {
        giftRecordService.insertOne(giftRecordDTO);
    }
}
