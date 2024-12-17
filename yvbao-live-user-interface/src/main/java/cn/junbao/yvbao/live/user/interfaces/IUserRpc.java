package cn.junbao.yvbao.live.user.interfaces;

import cn.junbao.yvbao.live.user.dto.UserDTO;

public interface IUserRpc {

    /**
     * 测试 ！！
     * @param name
     * @return
     */
    String sayHello(String name);

    UserDTO getUserById(Long userId);

}
