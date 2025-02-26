package cn.junbao.yubao.live.api.controller;

import cn.junbao.yubao.live.api.service.IUserLoginService;
import cn.junbao.yubao.live.api.vo.req.UserPhoneLoginReqVO;
import cn.junbao.yubao.live.common.interfaces.vo.WebResponseVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userLogin")
@Slf4j
public class UserLoginController {
    @Resource
    private IUserLoginService userLoginService;

    //发送验证码
    @PostMapping("/sendLoginCode")
    public WebResponseVO sendLoginCode(String phone) {
        return userLoginService.sendLoginCode(phone);
    }

    @PostMapping("/mobileLogin")
    public WebResponseVO mobileLogin(@RequestBody UserPhoneLoginReqVO userPhoneLoginReqVO, HttpServletResponse response){
        log.info("[mobileLogin] userPhoneLoginReqVO = {}",userPhoneLoginReqVO);
        return userLoginService.login(userPhoneLoginReqVO, response);
    }
}
