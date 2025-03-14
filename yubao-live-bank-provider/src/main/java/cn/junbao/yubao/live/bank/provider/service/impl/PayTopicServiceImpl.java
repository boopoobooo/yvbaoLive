package cn.junbao.yubao.live.bank.provider.service.impl;

import cn.junbao.yubao.live.bank.provider.dao.mapper.IPayTopicMapper;
import cn.junbao.yubao.live.bank.provider.dao.po.PayTopicPO;
import cn.junbao.yubao.live.bank.provider.service.IPayTopicService;
import cn.junbao.yubao.live.common.interfaces.enums.CommonStatusEum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @Author: Junbao
 * @Date: 2025/3/14 15:05
 * @Description:
 */
@Service
public class PayTopicServiceImpl implements IPayTopicService {


    @Resource
    private IPayTopicMapper payTopicMapper;

    @Override
    public PayTopicPO getByCode(Integer code) {
        PayTopicPO payTopicPOReq = new PayTopicPO();
        payTopicPOReq.setBizCode(code);
        payTopicPOReq.setStatus(CommonStatusEum.VALID_STATUS.getCode());
        return payTopicMapper.selectOne(payTopicPOReq);
    }
}
