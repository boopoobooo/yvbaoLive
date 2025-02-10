package cn.junbao.yubao.live.id.generate.rpc;

import cn.junbao.yubao.live.id.generate.interfaces.IdGenerateRpc;
import cn.junbao.yubao.live.id.generate.service.IdGenerateService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class IdGenerateRpcImpl implements IdGenerateRpc {
    @Resource
    private IdGenerateService idGenerateService;
    @Override
    public Long getSeqId(Integer id) {
        return idGenerateService.getSeqId(id);
    }

    @Override
    public Long getUnSeqId(Integer id) {
        return idGenerateService.getUnSeqId(id);
    }
}
