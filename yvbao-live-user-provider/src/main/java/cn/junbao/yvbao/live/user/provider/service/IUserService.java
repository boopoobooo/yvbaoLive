package cn.junbao.yvbao.live.user.provider.service;

import cn.junbao.yvbao.live.user.dto.UserDTO;

public interface IUserService {
    UserDTO getUserById(Long userId);
}
