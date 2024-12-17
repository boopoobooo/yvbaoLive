package cn.junbao.yvbao.live.user.provider.service.impl;

import cn.junbao.yvbao.live.user.dto.UserDTO;
import cn.junbao.yvbao.live.user.provider.dao.mapper.IUserMapper;
import cn.junbao.yvbao.live.user.provider.dao.po.UserPO;
import cn.junbao.yvbao.live.user.provider.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserMapper userMapper;

    public UserServiceImpl(IUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO getUserById(Long userId) {
        UserPO user = userMapper.getUserById(userId);

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setNickName(user.getNickName());
        userDTO.setTrueName(user.getTrueName());
        userDTO.setAvatar(user.getAvatar());
        userDTO.setWorkCity(user.getWorkCity());
        userDTO.setBornCity(user.getBornCity());
        userDTO.setBornDate(user.getBornDate());
        userDTO.setCreateTime(user.getCreateTime());
        userDTO.setUpdateTime(user.getUpdateTime());

        return userDTO;
    }
}
