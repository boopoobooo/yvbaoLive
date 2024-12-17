package cn.junbao.yvbao.live.api.controller;

import cn.junbao.yvbao.live.user.dto.UserDTO;
import cn.junbao.yvbao.live.user.interfaces.IUserRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @DubboReference
    private IUserRpc userRpc;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/getUserInfo")
    public UserDTO getUserInfo(@RequestParam Long userId){
        logger.info("API   getUserInfo 开始,{}",userId);
        return userRpc.getUserById(userId);
    }
}
