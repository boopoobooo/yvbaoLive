package cn.junbao.yubao.live.api.service;

import cn.junbao.yubao.live.api.vo.req.UserPhoneLoginReqVO;
import cn.junbao.yubao.live.common.interfaces.vo.WebResponseVO;
import jakarta.servlet.http.HttpServletResponse;

public interface IUserLoginService {
    WebResponseVO sendLoginCode(String phone);
    WebResponseVO login(UserPhoneLoginReqVO userPhoneLoginReqVO, HttpServletResponse response);
}
