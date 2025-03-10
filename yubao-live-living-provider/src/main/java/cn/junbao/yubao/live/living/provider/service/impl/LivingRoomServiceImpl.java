package cn.junbao.yubao.live.living.provider.service.impl;

import cn.junbao.yubao.im.core.server.dto.ImOfflineDTO;
import cn.junbao.yubao.im.core.server.dto.ImOnlineDTO;
import cn.junbao.yubao.live.common.interfaces.enums.CommonStatusEum;
import cn.junbao.yubao.live.common.interfaces.utils.ConvertBeanUtils;
import cn.junbao.yubao.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import cn.junbao.yubao.live.framework.web.strater.context.WebRequestContext;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomReqDTO;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomRespDTO;
import cn.junbao.yubao.live.living.interfaces.enums.LivingRoomTypeEnum;
import cn.junbao.yubao.live.living.provider.dao.mapper.ILivingRoomMapper;
import cn.junbao.yubao.live.living.provider.dao.mapper.ILivingRoomRecordMapper;
import cn.junbao.yubao.live.living.provider.dao.po.LivingRoomPO;
import cn.junbao.yubao.live.living.provider.dao.po.LivingRoomRecordPO;
import cn.junbao.yubao.live.living.provider.service.ILivingRoomService;
import cn.junbao.yubao.live.user.interfaces.IUserRpc;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Junbao
 * @Date: 2025/2/22 23:32
 * @Description:
 */
@Service
@Slf4j
public class LivingRoomServiceImpl implements ILivingRoomService {

    @DubboReference(check = false)
    private IUserRpc userRpc;

    @Resource
    private ILivingRoomMapper livingRoomMapper;

    @Resource
    private ILivingRoomRecordMapper livingRoomRecordMapper;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private LivingProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO) {
        Integer roomId = livingRoomReqDTO.getRoomId();
        Integer appId = livingRoomReqDTO.getAppId();
        String cacheKey = cacheKeyBuilder.buildLivingRoomUserSetKey(roomId);
        log.info("[queryUserIdByRoomId]cacheKey = {}",cacheKey);
        //通过多次scan，避免一次性获取大量数据导致可能的卡顿
        Cursor<Object> cursor = redisTemplate.opsForSet().scan(cacheKey, ScanOptions.scanOptions().match("*").count(100).build());
        List<Long> userIdList = new ArrayList<>();
        while (cursor.hasNext()) {
            Integer userId = (Integer) cursor.next();
            userIdList.add(Long.valueOf(userId));
        }
        log.info("[queryUserIdByRoomId] userIdList size = {}",userIdList.size());
        return userIdList;
    }

    @Override
    public void userOfflineHandler(ImOfflineDTO imOfflineDTO) {
        log.info("[userOfflineHandler]imOfflineDTO is {}", imOfflineDTO);
        Long userId = imOfflineDTO.getUserId();
        Integer roomId = imOfflineDTO.getRoomId();
        Integer appId = imOfflineDTO.getAppId();
        String cacheKey = cacheKeyBuilder.buildLivingRoomUserSetKey(roomId);
        redisTemplate.delete(cacheKey);
    }

    @Override
    public void userOnlineHandler(ImOnlineDTO imOnlineDTO) {
        log.info("[userOnlineHandler]imOnlineDTO is {}", imOnlineDTO);
        Long userId = imOnlineDTO.getUserId();
        Integer roomId = imOnlineDTO.getRoomId();
        Integer appId = imOnlineDTO.getAppId();
        String cacheKey = cacheKeyBuilder.buildLivingRoomUserSetKey(roomId);
        //set集合中
        redisTemplate.opsForSet().add(cacheKey, userId);
        redisTemplate.expire(cacheKey, 12, TimeUnit.HOURS);
    }

    @Override
    public LivingRoomRespDTO queryByRoomId(Integer roomId) {
        log.info("[queryByRoomId]  roomId = {}",roomId);
        if (roomId == null ){
            return null;
        }
        String cacheKey = cacheKeyBuilder.buildLivingRoomObjKey(roomId);
        LivingRoomRespDTO cacheResult = (LivingRoomRespDTO) redisTemplate.opsForValue().get(cacheKey);
        if (cacheResult != null) {
            //空值缓存
            if (cacheResult.getId() == null) {
                return null;
            }
            return cacheResult;
        }

        LivingRoomPO livingRoomPO = livingRoomMapper.selectOneByRoomId(roomId);
        LivingRoomRespDTO queryResult = ConvertBeanUtils.convert(livingRoomPO, LivingRoomRespDTO.class);
        if (queryResult == null) {
            log.warn("[queryByRoomId] roomId = {} not found",roomId);
            //防止缓存击穿
            redisTemplate.opsForValue().set(cacheKey, new LivingRoomRespDTO(), 1, TimeUnit.MINUTES);
            return null;
        }
        redisTemplate.opsForValue().set(cacheKey, queryResult, 30, TimeUnit.MINUTES);
        return queryResult;
    }

    @Override
    public Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO) {

        LivingRoomPO livingRoomPO = ConvertBeanUtils.convert(livingRoomReqDTO, LivingRoomPO.class);
        livingRoomPO.setStatus(CommonStatusEum.VALID_STATUS.getCode());
        livingRoomPO.setStartTime(new Date());
        livingRoomMapper.insertOne(livingRoomPO);
        String cacheKey = cacheKeyBuilder.buildLivingRoomObjKey(livingRoomPO.getRoomId());
        //删除空值
        redisTemplate.delete(cacheKey);
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
        livingRoomRecordPO.setStatus(CommonStatusEum.VALID_STATUS.getCode());
        livingRoomRecordPO.setEndTime(new Date());
        livingRoomRecordMapper.insertOne(livingRoomRecordPO);
        livingRoomMapper.deleteByRoomId(roomId);
        String cacheKey = cacheKeyBuilder.buildLivingRoomObjKey(roomId);
        redisTemplate.delete(cacheKey);
        return true;
    }

    @Override
    public List<LivingRoomRespDTO> getLivingRoomPage(Integer type, int pageNum, int pageSize) {
        String cacheKey = cacheKeyBuilder.buildLivingRoomListKey(type);
        int offset = (pageNum - 1) * pageSize;
        //查询缓存
        List<Object> cacheLivingRoomList = redisTemplate.opsForList().range(cacheKey, offset, pageSize);
        if (cacheLivingRoomList!= null && !cacheLivingRoomList.isEmpty()){
            return ConvertBeanUtils.convertList(cacheLivingRoomList, LivingRoomRespDTO.class);
        }else {
            //为空
            return Collections.emptyList();
        }
    }


    @Override
    public List<LivingRoomRespDTO> listAllLivingRoomFromDB(Integer type) {
        List<LivingRoomPO> livingRoomPOList =  livingRoomMapper.selectAll( type);
        return ConvertBeanUtils.convertList(livingRoomPOList, LivingRoomRespDTO.class);
    }
}
