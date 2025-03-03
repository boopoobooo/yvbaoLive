package cn.junbao.yubao.live.api.controller;

import cn.junbao.yubao.live.common.interfaces.vo.WebResponseVO;
import cn.junbao.yubao.live.user.dto.UserDTO;
import cn.junbao.yubao.live.user.interfaces.IUserRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {
    @DubboReference(check = false)
    private IUserRpc userRpc;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/getUserInfo")
    public WebResponseVO getUserInfo(@RequestParam Long userId){
        logger.info("getUserInfo 开始,{}",userId);
        UserDTO userDTO = userRpc.getUserById(userId);
        return WebResponseVO.success(userDTO);
    }

    @GetMapping("/batchQueryUserInfo")
    public WebResponseVO batchQueryUserInfo(String userIdsStr){
        List<Long> userIds = Arrays.asList(userIdsStr.split(",")).stream().map(x -> Long.valueOf(x)).collect(Collectors.toList());
        Map<Long, UserDTO> longUserDTOMap = userRpc.batchQueryUserInfo(userIds);
        return WebResponseVO.success();
    }

    @GetMapping("/test")
    public String test(){
        logger.info("===============test================");
        return "success";
    }

}
