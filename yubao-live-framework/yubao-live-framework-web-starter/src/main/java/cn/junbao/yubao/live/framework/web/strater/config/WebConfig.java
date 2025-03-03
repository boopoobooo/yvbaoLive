package cn.junbao.yubao.live.framework.web.strater.config;

import cn.junbao.yubao.live.framework.web.strater.interceptor.UserInfoInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Bean
    public UserInfoInterceptor userInfoInterceptor(){
        return new UserInfoInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInfoInterceptor())
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns("/static/**", "/css/**", "/js/**", "/images/**"); // 排除静态资源路径
    }
}
