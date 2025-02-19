package cn.junbao.yubao.im.router.provider;

import cn.junbao.yubao.im.router.provider.service.ImRouterService;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: Junbao
 * @Date: 2025/2/18 17:10
 * @Description:
 */
@SpringBootApplication
@EnableDubbo
public class ImRouterProviderApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ImRouterProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    @Resource
    private ImRouterService imRouterService;
    @Override
    public void run(String... args) throws Exception {
        ImMsgBody imMsgBody =new ImMsgBody();
        imRouterService.sendMsg(imMsgBody);
        System.out.println("========test=======END===============");
        Thread.sleep(3000);
    }
}
