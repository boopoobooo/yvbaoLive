package cn.junbao.yubao.live.framework.web.strater.interceptor;

import cn.junbao.yubao.live.framework.web.strater.annotation.RequestLimit;
import cn.junbao.yubao.live.framework.web.strater.context.WebRequestContext;
import cn.junbao.yubao.live.framework.web.strater.exception.YuBaoErrorException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Junbao
 * @Date: 2025/3/18 20:04  对于重复请求，要有专门的拦截器去处理，进行相同用户下的限流
 * @Description:
 */
@Component
@Slf4j
public class RequestLimitInterceptor implements HandlerInterceptor {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 查看有无 @RequestLimit 注解标识该方法
            boolean hasLimit = handlerMethod.getMethod().isAnnotationPresent(RequestLimit.class);
            if (hasLimit) {
                RequestLimit requestLimit = handlerMethod.getMethod().getAnnotation(RequestLimit.class);
                Long userId = WebRequestContext.getUserId();
                // 没有userId标识是网关通过的白名单请求，放行
                if (userId == null) {
                    return true;
                }
                //设置缓存的key
                String cacheKey = applicationName + ":" + userId + ":" + request.getRequestURI();
                int limit = requestLimit.limit();// 限制访问数量上限
                int second = requestLimit.second();// 时间限制
                Integer reqTime = (Integer) Optional.ofNullable(redisTemplate.opsForValue().get(cacheKey)).orElse(0);
                if (reqTime == 0) {
                    redisTemplate.opsForValue().set(cacheKey, 1, second, TimeUnit.SECONDS);
                    return true;
                } else if (reqTime < limit) {
                    redisTemplate.opsForValue().increment(cacheKey, 1);
                    return true;
                }
                // 超过限流数量上限
                // 直接抛出全局异常，让异常捕获器处理
                log.error("[RequestLimitInterceptor] userId is {}, req too much", userId);
                throw new YuBaoErrorException(-10001, requestLimit.msg());
            }
        }
        return true;
    }

}
