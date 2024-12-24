package cn.junbao.yvbao.live.user.interfaces;

import cn.junbao.yvbao.live.user.dto.UserDTO;

import java.util.List;
import java.util.Map;

public interface IUserRpc {

    /**
     * 测试 ！！
     * @param name
     * @return
     */
    String sayHello(String name);

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    UserDTO getUserById(Long userId);

    Map<Long,UserDTO> batchQueryUserInfo(List<Long> userIdList);

}
