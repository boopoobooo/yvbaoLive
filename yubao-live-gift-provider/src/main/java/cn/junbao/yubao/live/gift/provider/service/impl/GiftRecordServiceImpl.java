package cn.junbao.yubao.live.gift.provider.service.impl;

import cn.junbao.yubao.live.common.interfaces.utils.ConvertBeanUtils;
import cn.junbao.yubao.live.gift.dto.GiftRecordDTO;
import cn.junbao.yubao.live.gift.provider.dao.mapper.IGiftRecordMapper;
import cn.junbao.yubao.live.gift.provider.dao.po.GiftRecordPO;
import cn.junbao.yubao.live.gift.provider.service.IGiftRecordService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class GiftRecordServiceImpl implements IGiftRecordService {

    @Resource
    private IGiftRecordMapper giftRecordMapper;

    @Override
    public void insertOne(GiftRecordDTO giftRecordDTO) {
        GiftRecordPO giftRecordPO = ConvertBeanUtils.convert(giftRecordDTO,GiftRecordPO.class);
        giftRecordMapper.insertOne(giftRecordPO);
    }
}
