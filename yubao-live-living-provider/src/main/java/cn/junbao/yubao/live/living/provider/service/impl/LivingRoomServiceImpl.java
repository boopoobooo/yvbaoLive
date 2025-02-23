package cn.junbao.yubao.live.living.provider.service.impl;

import cn.junbao.yubao.live.common.interfaces.enums.CommonStatusEum;
import cn.junbao.yubao.live.common.interfaces.utils.ConvertBeanUtils;
import cn.junbao.yubao.live.framework.web.strater.context.WebRequestContext;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomReqDTO;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomRespDTO;
import cn.junbao.yubao.live.living.provider.dao.mapper.ILivingRoomMapper;
import cn.junbao.yubao.live.living.provider.dao.mapper.ILivingRoomRecordMapper;
import cn.junbao.yubao.live.living.provider.dao.po.LivingRoomPO;
import cn.junbao.yubao.live.living.provider.dao.po.LivingRoomRecordPO;
import cn.junbao.yubao.live.living.provider.service.ILivingRoomService;
import cn.junbao.yubao.live.user.interfaces.IUserRpc;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: Junbao
 * @Date: 2025/2/22 23:32
 * @Description:
 */
@Service
@Slf4j
public class LivingRoomServiceImpl implements ILivingRoomService {

    @DubboReference
    private IUserRpc userRpc;

    @Resource
    private ILivingRoomMapper livingRoomMapper;

    @Resource
    private ILivingRoomRecordMapper livingRoomRecordMapper;

    @Override
    public LivingRoomRespDTO queryByRoomId(Integer roomId) {
        return null;
    }

    @Override
    public Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO) {

        LivingRoomPO livingRoomPO = ConvertBeanUtils.convert(livingRoomReqDTO, LivingRoomPO.class);
        livingRoomPO.setStatus(CommonStatusEum.VALID_STATUS.getCode());
        livingRoomPO.setStartTime(new Date());
        livingRoomMapper.insertOne(livingRoomPO);
        return livingRoomPO.getRoomId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO) {
        Long anchorId = livingRoomReqDTO.getAnchorId();
        Integer roomId = livingRoomReqDTO.getRoomId();

        LivingRoomPO livingRoomPO= livingRoomMapper.selectByRoomId(roomId);
        if (livingRoomPO == null){
            return false;
        }
        if (!anchorId.equals(livingRoomPO.getAnchorId())){
            log.warn("[closeLiving] 当前用户不是直播间拥有者...");
            return false;
        }

        LivingRoomRecordPO livingRoomRecordPO = ConvertBeanUtils.convert(livingRoomPO, LivingRoomRecordPO.class);
        livingRoomRecordPO.setEndTime(new Date());
        livingRoomRecordMapper.insertOne(livingRoomRecordPO);
        livingRoomMapper.deleteByRoomId(roomId);
        return true;
    }

    @Override
    public List<LivingRoomRespDTO> getLivingRoomPage(Integer type, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<LivingRoomPO> livingRoomPOList = livingRoomMapper.selectLivingRoomPage(type,offset, pageSize);
        List<LivingRoomRespDTO> livingRoomRespDTOList = new ArrayList<>();

        for (LivingRoomPO livingRoomPO : livingRoomPOList) {
            LivingRoomRespDTO livingRoomRespDTO = ConvertBeanUtils.convert(livingRoomPO, LivingRoomRespDTO.class);
            livingRoomRespDTOList.add(livingRoomRespDTO);
        }
        return livingRoomRespDTOList;
    }
}
