package cn.junbao.yubao.live.im.core.server;

import cn.junbao.yubao.live.im.constants.AppIdEnum;
import cn.junbao.yubao.live.im.core.server.service.IMsgAckCheckService;
import cn.junbao.yubao.live.im.dto.ImMsgBody;
import cn.junbao.yubao.live.mq.starter.publisher.EventPublisher;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class ImCoreServerApplication {
    public static void main(String[] args) {
        SpringApplication springApplication =new SpringApplication(ImCoreServerApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private IMsgAckCheckService msgAckCheckService;
    /*@Override
    public void run(String... args) throws Exception {
        *//*ImMsgBody imMsgBody = new ImMsgBody();
        imMsgBody.setUserId(10001l);
        imMsgBody.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
        msgAckCheckService.sendDelayMsg(imMsgBody);*//*
    }*/
}

