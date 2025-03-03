package cn.junbao.yubao.live.api.service.impl;

import cn.junbao.yubao.live.api.service.IUserLoginService;
import cn.junbao.yubao.live.api.vo.UserLoginVO;
import cn.junbao.yubao.live.api.vo.req.UserPhoneLoginReqVO;
import cn.junbao.yubao.live.common.interfaces.enums.WebRequestEnum;
import cn.junbao.yubao.live.common.interfaces.utils.ConvertBeanUtils;
import cn.junbao.yubao.live.common.interfaces.vo.WebResponseVO;
import cn.junbao.yubao.live.msg.dto.MsgCheckDTO;
import cn.junbao.yubao.live.msg.enums.MsgSendResultEnum;
import cn.junbao.yubao.live.msg.interfaces.ISmsRpc;
import cn.junbao.yubao.live.user.dto.UserLoginDTO;
import cn.junbao.yubao.live.user.interfaces.IUserAccountRpc;
import cn.junbao.yubao.live.user.interfaces.IUserPhoneRpc;
import com.alibaba.cloud.commons.lang.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
public class UserLoginServiceImpl implements IUserLoginService {

    private static final String PHONE_REG = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";
    @DubboReference(check = false)
    private ISmsRpc smsRpc ;
    @DubboReference(check = false)
    private IUserPhoneRpc userPhoneRpc;
    @DubboReference(check = false)
    private IUserAccountRpc userAccountRpc;


//    @DubboReference(check = false)
//    private IAccountTokenRpc accountTokenRpc;

    @Override
    public WebResponseVO sendLoginCode(String phone) {
        log.info("[sendLoginCode] phoneNumber = {}",phone);
        //1. 校验手机号
        if (StringUtils.isBlank(phone)){
            return WebResponseVO.errorParam("手机号格式不正确:"+phone);
        }
        //2. 发送验证码
        MsgSendResultEnum msgSendResultEnum = smsRpc.sendLoginCode(phone);
        if (msgSendResultEnum == MsgSendResultEnum.SEND_SUCCESS) {
            log.info("[sendLoginCode] 验证码发送成功,phone= {}",phone);
            return WebResponseVO.success();
        }
        log.warn("[sendLoginCode] 验证码发送失败==phone={}",phone);
        return WebResponseVO.sysError("短信发送失败，请稍后再试");
    }

    @Override
    public WebResponseVO login(UserPhoneLoginReqVO userPhoneLoginReqVO, HttpServletResponse response) {
        String phone = userPhoneLoginReqVO.getPhone();
        String code = userPhoneLoginReqVO.getCode();
        log.info("[login] 用户手机号登录: phone = {},code = {}",phone,code);
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
        log.info("[login] 验证码校验通过..当前phone:{}",phone);
        //验证码校验通过
        UserLoginDTO userLoginDTO = userPhoneRpc.login(phone);
        log.info("[login]userPhoneRpc.login的结果为 useId = {}",userLoginDTO.getUserId());
        //生成token
        String loginToken = userAccountRpc.createAndSaveToken(userLoginDTO.getUserId());
        this.saveTokenInCookie(loginToken,response);

        return WebResponseVO.success(ConvertBeanUtils.convert(userLoginDTO, UserLoginVO.class));
    }

    /**
     * 在cookie中保存登录token
     */
    private void saveTokenInCookie(String loginToken,HttpServletResponse response){
        Cookie cookie = new Cookie(WebRequestEnum.COOKIE_TOKEN_NAME.getName(), loginToken);
        //设置顶级域名，接着我们的二级域名中进行web页面的访问，后续请求就会携带上token
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 3600);
        response.addCookie(cookie);
        log.info("[saveTokenInCookie] cookie设置成功");
    }
}
