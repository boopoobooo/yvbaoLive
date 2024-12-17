package cn.junbao.yvbao.live.api;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class ApiWebApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ApiWebApplication.class);
        springApplication.run(args);
    }
}
