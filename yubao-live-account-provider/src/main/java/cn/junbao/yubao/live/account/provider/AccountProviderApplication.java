package cn.junbao.yubao.live.account.provider;

import cn.junbao.yubao.live.account.provider.service.IAccountTokenService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class AccountProviderApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(AccountProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    @Resource
    private IAccountTokenService accountTokenService;

    @Override
    public void run(String... args) throws Exception {
        Long userId = 10321L;
        String token = accountTokenService.createAndSaveToken(userId);
        System.out.println("========token === " + token);
        Long userIdByToken = accountTokenService.getUserIdByToken(token);
        System.out.println("======userid = ==== "+ userIdByToken);

    }
}
