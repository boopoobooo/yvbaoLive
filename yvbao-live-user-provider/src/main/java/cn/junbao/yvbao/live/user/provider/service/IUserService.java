package cn.junbao.yvbao.live.user.provider.service;

import cn.junbao.yvbao.live.user.dto.UserDTO;

import java.util.List;
import java.util.Map;

public interface IUserService {
    UserDTO getUserById(Long userId);

    Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList);
}
