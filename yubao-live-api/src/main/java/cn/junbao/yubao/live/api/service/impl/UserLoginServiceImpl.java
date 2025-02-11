package cn.junbao.yubao.live.api.service.impl;

import cn.junbao.yubao.live.api.service.IUserLoginService;
import cn.junbao.yubao.live.api.vo.UserLoginVO;
import cn.junbao.yubao.live.common.interfaces.utils.ConvertBeanUtils;
import cn.junbao.yubao.live.common.interfaces.vo.WebResponseVO;
import cn.junbao.yubao.live.msg.dto.MsgCheckDTO;
import cn.junbao.yubao.live.msg.enums.MsgSendResultEnum;
import cn.junbao.yubao.live.msg.interfaces.ISmsRpc;
import cn.junbao.yubao.live.user.dto.UserLoginDTO;
import cn.junbao.yubao.live.user.interfaces.IUserPhoneRpc;
import com.alibaba.cloud.commons.lang.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserLoginServiceImpl implements IUserLoginService {

    private static String PHONE_REG = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";
    @DubboReference
    private ISmsRpc smsRpc ;
    @DubboReference
    private IUserPhoneRpc userPhoneRpc;

    @Override
    public WebResponseVO sendLoginCode(String phone) {
        //1. 校验手机号
        if (StringUtils.isBlank(phone)){
            return WebResponseVO.errorParam("手机号格式不正确:"+phone);
        }
        //2. 发送验证码
        MsgSendResultEnum msgSendResultEnum = smsRpc.sendLoginCode(phone);
        if (msgSendResultEnum == MsgSendResultEnum.SEND_SUCCESS) {
            return WebResponseVO.success();
        }
        return WebResponseVO.sysError("短信发送太频繁，请稍后再试");
    }

    @Override
    public WebResponseVO login(String phone, String code, HttpServletResponse response) {
        //1. 校验格式
        if (StringUtils.isEmpty(phone)) {
            return WebResponseVO.errorParam("手机号不能为空");
        }
        if (!Pattern.matches(PHONE_REG, phone)) {
            return WebResponseVO.errorParam("手机号格式异常");
        }
        if (code == null || code.length() < 6 ) {
            return WebResponseVO.errorParam("验证码格式异常");
        }

        //2.校验验证码
        MsgCheckDTO msgCheckDTO = smsRpc.checkLoginCode(phone, code);
        if (!msgCheckDTO.isCheckStatus()) {
            return WebResponseVO.bizError(msgCheckDTO.getDesc());
        }
        //验证码校验通过
        UserLoginDTO userLoginDTO = userPhoneRpc.login(phone);
        Cookie cookie = new Cookie("yubao_token", userLoginDTO.getToken());
        //设置顶级域名，接着我们的二级域名中进行web页面的访问，后续请求就会携带上它了
        cookie.setDomain("yubao.live.com");
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 3600);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.addCookie(cookie);
        return WebResponseVO.success(ConvertBeanUtils.convert(userLoginDTO, UserLoginVO.class));
    }
}
