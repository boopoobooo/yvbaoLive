package cn.junbao.yvbao.live.user.provider.service.impl;

import cn.junbao.yvbao.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import cn.junbao.yvbao.live.user.dto.UserDTO;
import cn.junbao.yvbao.live.user.provider.dao.mapper.IUserMapper;
import cn.junbao.yvbao.live.user.provider.dao.po.UserPO;
import cn.junbao.yvbao.live.user.provider.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {
    @Resource
    private IUserMapper userMapper;
    @Resource
    private RedisTemplate<String,UserDTO> redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public UserDTO getUserById(Long userId) {

        if (userId == null){
            return null;
        }
        String cacheKey = cacheKeyBuilder.buildUserInfoKey(userId);
        //String cacheKey = " user-"+userId;
        UserDTO userDTO = redisTemplate.opsForValue().get(cacheKey);
        if (userDTO != null) {
            return userDTO;
        }

        UserPO user = userMapper.getUserById(userId);
        if (user != null){
            userDTO = new UserDTO();
            userDTO.setUserId(user.getUserId());
            userDTO.setNickName(user.getNickName());
            userDTO.setTrueName(user.getTrueName());
            userDTO.setAvatar(user.getAvatar());
            userDTO.setWorkCity(user.getWorkCity());
            userDTO.setBornCity(user.getBornCity());
            userDTO.setBornDate(user.getBornDate());
            userDTO.setCreateTime(user.getCreateTime());
            userDTO.setUpdateTime(user.getUpdateTime());
            redisTemplate.opsForValue().set(cacheKey,userDTO);
        }
        return userDTO;
    }
}
