package cn.junbao.yubao.live.user.provider.service.impl;

import cn.junbao.yubao.live.common.interfaces.utils.ConvertBeanUtils;
import cn.junbao.yubao.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import cn.junbao.yubao.live.user.constants.UserTagFieldNameConstants;
import cn.junbao.yubao.live.user.constants.UserTagsEnum;
import cn.junbao.yubao.live.user.dto.UserTagDTO;
import cn.junbao.yubao.live.user.provider.dao.mapper.IUserTagMapper;
import cn.junbao.yubao.live.user.provider.dao.po.UserTagPO;
import cn.junbao.yubao.live.user.provider.service.IUserTagService;
import cn.junbao.yubao.live.user.provider.utils.TagInfoUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserTagServiceImpl implements IUserTagService {

    @Resource
    private IUserTagMapper userTagMapper;
    @Resource
    private RedisTemplate<String, UserTagDTO> redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;


    @Override
    public boolean setTag(Long userId, UserTagsEnum userTagsEnum) {

        int updateStatus = userTagMapper.setTag(userId,userTagsEnum.getFieldName(),userTagsEnum.getTag());
        if (updateStatus > 0 ){
            redisTemplate.delete(userProviderCacheKeyBuilder.buildUserTagKey(userId));
            return true;
        }
        //1. 未更新成功，可能情况：  1. 已经存在该标签 2. tag表中没有该用户数据
        //并发场景应对
        String userTagLockKey = userProviderCacheKeyBuilder.buildUserTagLock(userId);

        String userTagLockStatus = redisTemplate.execute(new RedisCallback<String>() {
            //保证setnx操作的原子性
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
                return (String) connection.execute("set", keySerializer.serialize(userTagLockKey), valueSerializer.serialize("-1"),
                        "NX".getBytes(StandardCharsets.UTF_8),
                        "EX".getBytes(StandardCharsets.UTF_8),
                        "3".getBytes(StandardCharsets.UTF_8));
            }
        });
        log.info("当前userTagLockStatus ====== "+ userTagLockStatus);
        if (! "ok".equalsIgnoreCase(userTagLockStatus)){
            return false;
        }

        UserTagPO userTagPO = userTagMapper.selectByUserId(userId);
        if (userTagPO != null){
            //tag表中有该数据， 则已经存在该标签
            return false;
        }

        //表中没有该用户数据，插入该用户数据
        userTagMapper.insert(userId);
        updateStatus = userTagMapper.setTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag());
        log.info("[setTag] 设置tag成功！！");
        redisTemplate.delete(userTagLockKey);
        redisTemplate.delete(userProviderCacheKeyBuilder.buildUserTagKey(userId));
        return updateStatus > 0 ;
    }

    @Override
    public boolean cancelTag(Long userId, UserTagsEnum userTagsEnum) {

        int cancelStatus = userTagMapper.cancelTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag());
        if (cancelStatus <= 0 ){
            return false;
        }

        redisTemplate.delete(userProviderCacheKeyBuilder.buildUserTagKey(userId));
        return true;
    }

    @Override
    public boolean containTag(Long userId, UserTagsEnum userTagsEnum) {

        UserTagDTO userTagDTO = this.queryUserTag(userId);
        if (userTagDTO == null){
            return false;
        }
        String targetFieldName = userTagsEnum.getFieldName();
        if (targetFieldName.equals(UserTagFieldNameConstants.TAG_INFO_01)){
            return TagInfoUtil.isContainTag(userTagDTO.getTagInfo01(),userTagsEnum.getTag());
        }else if (targetFieldName.equals(UserTagFieldNameConstants.TAG_INFO_02)){
            return TagInfoUtil.isContainTag(userTagDTO.getTagInfo02(),userTagsEnum.getTag());
        }else if (targetFieldName.equals(UserTagFieldNameConstants.TAG_INFO_03)){
            return TagInfoUtil.isContainTag(userTagDTO.getTagInfo03(),userTagsEnum.getTag());
        }
        return false;
    }

    /**
     * 从缓存和DB中查询 userTag
     * @param userId
     * @return
     */
    private UserTagDTO queryUserTag(Long userId){
        String cacheKey = userProviderCacheKeyBuilder.buildUserTagKey(userId);
        UserTagDTO userTagDTO = redisTemplate.opsForValue().get(cacheKey);
        if (userTagDTO != null ){
            return userTagDTO;
        }
        UserTagPO userTagPO = userTagMapper.selectByUserId(userId);
        if (userTagPO == null){
            return null;
        }
        userTagDTO = ConvertBeanUtils.convert(userTagPO,UserTagDTO.class);
        redisTemplate.opsForValue().set(cacheKey,userTagDTO);
        redisTemplate.expire(cacheKey,30, TimeUnit.MINUTES);
        return userTagDTO;
    }
}
