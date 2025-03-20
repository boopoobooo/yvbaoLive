package cn.junbao.yubao.live.living.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: Junbao
 * @Description:
 */
@SpringBootApplication
@EnableDubbo
public class LivingRoomApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(LivingRoomApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

}
