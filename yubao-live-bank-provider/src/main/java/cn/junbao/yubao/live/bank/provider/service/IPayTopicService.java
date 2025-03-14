package cn.junbao.yubao.live.bank.provider.service;

import cn.junbao.yubao.live.bank.provider.dao.po.PayTopicPO;

public interface IPayTopicService {
    PayTopicPO getByCode(Integer code);
}
