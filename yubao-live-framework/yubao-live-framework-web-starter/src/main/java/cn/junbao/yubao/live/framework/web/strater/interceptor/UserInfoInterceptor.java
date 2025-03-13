package cn.junbao.yubao.live.framework.web.strater.interceptor;

import cn.junbao.yubao.live.common.interfaces.enums.GatewayHeaderEnum;
import cn.junbao.yubao.live.common.interfaces.enums.WebRequestEnum;
import cn.junbao.yubao.live.framework.web.strater.context.WebRequestContext;
import com.alibaba.cloud.commons.lang.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
@Component
@Slf4j
public class UserInfoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userIdStr = request.getHeader(GatewayHeaderEnum.USER_LOGIN_ID.getName());
        if (StringUtils.isBlank(userIdStr)){
            //白名单路径,放行
            return true;
        }
        //有效userid,放入线程本地变量中
        WebRequestContext.set(WebRequestEnum.WEB_REQUEST_USER_ID.getName(),Long.valueOf(userIdStr));

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        WebRequestContext.clear();//清楚线程本地变量
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
