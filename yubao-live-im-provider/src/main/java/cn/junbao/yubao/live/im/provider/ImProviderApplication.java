package cn.junbao.yubao.live.im.provider;

import cn.junbao.yubao.live.im.constants.AppIdEnum;
import cn.junbao.yubao.live.im.provider.service.ImTokenService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class ImProviderApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ImProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    @Resource
    private ImTokenService imTokenService;
    @Override
    public void run(String... args) throws Exception {
        Long userId = 132123L;
        String token = imTokenService.createImLoginToken(userId, AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
        System.out.println("token === "+token);
        Long userIdByToken = imTokenService.getUserIdByToken(token);
        System.out.println("userid = "+userIdByToken);
    }
}
