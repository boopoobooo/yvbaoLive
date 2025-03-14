package cn.junbao.yubao.live.gateway.filter;

import cn.junbao.yubao.live.common.interfaces.enums.GatewayHeaderEnum;
import cn.junbao.yubao.live.common.interfaces.enums.WebRequestEnum;
import cn.junbao.yubao.live.gateway.properties.YuBaoGatewayProperties;
import cn.junbao.yubao.live.user.interfaces.IUserAccountRpc;
import com.alibaba.cloud.commons.lang.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static io.netty.handler.codec.http.cookie.CookieHeaderNames.MAX_AGE;
import static org.springframework.web.cors.CorsConfiguration.ALL;

@Component
@Slf4j
public class AccountCheckFilter implements GlobalFilter, Ordered {
    @Resource
    private YuBaoGatewayProperties yuBaoGatewayProperties;

    @DubboReference(check = false)
    private IUserAccountRpc userAccountRpc;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestPath = request.getPath().value();
        ServerHttpResponse response = exchange.getResponse();
        //跨域设置 【暂时写死】
        HttpHeaders headers = response.getHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://web.yubao.live.com:5500");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ALL);
        headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);

        if (StringUtils.isBlank(requestPath)){
            log.warn("[AccountCheckFilter]当前请求路径为空");
            return Mono.empty();
        }

        // 检查请求路径是否在白名单中或以白名单路径为前缀
        List<String> whiteList = yuBaoGatewayProperties.getWhileList();
        for (String whiteListPath : whiteList) {
            if (requestPath.startsWith(whiteListPath)) {
                log.info("请求路径 {} matches whitelist prefix {}, allowing access", requestPath, whiteListPath);
                return chain.filter(exchange); // 放行
            }
        }

        //取出cookie并进行校验
        List<HttpCookie> httpCookies = request.getCookies().get(WebRequestEnum.COOKIE_TOKEN_NAME.getName());
        if (CollectionUtils.isEmpty(httpCookies)){
            log.warn("[AccountCheckFilter] httpCookies is null!没有检索到对应的token,requestPath:{}",requestPath);
            return Mono.empty();
        }
        String token = httpCookies.get(0).getValue();
        if (StringUtils.isBlank(token)){
            log.warn("[AccountCheckFilter] token 为空");
            return Mono.empty();
        }
        //校验cookie
        Long userId = userAccountRpc.getUserIdByToken(token);
        if (userId == null) {
            log.warn("[AccountCheckFilter]cookie校验失败,cookie已经失效");
            return Mono.empty();
        }
        //校验成功，将userid传入下游并放行
        ServerHttpRequest.Builder builder = request.mutate();
        builder.header(GatewayHeaderEnum.USER_LOGIN_ID.getName(), String.valueOf(userId));
        log.debug("[AccountCheckFilter]请求头信息：{}",builder.build().getHeaders().get(GatewayHeaderEnum.USER_LOGIN_ID.getName()));
        return chain.filter(exchange.mutate().request(builder.build()).build());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
