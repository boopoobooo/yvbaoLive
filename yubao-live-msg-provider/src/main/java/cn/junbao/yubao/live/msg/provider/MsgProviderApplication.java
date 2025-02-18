package cn.junbao.yubao.live.msg.provider;

import cn.junbao.yubao.live.msg.dto.MsgCheckDTO;
import cn.junbao.yubao.live.msg.provider.service.ISmsService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
@EnableDubbo
public class MsgProviderApplication implements CommandLineRunner {

    @Resource
    private ISmsService smsService;
    public static void main(String[] args) {
        SpringApplication springApplication =new SpringApplication(MsgProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }


    @Override
    public void run(String... args) throws Exception {
        String phone = "13878594448";
        smsService.sendLoginCode(phone );
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入：");
        String code = sc.next();
        MsgCheckDTO msgCheckDTO = smsService.checkLoginCode(phone, code);
        System.out.println(msgCheckDTO.isCheckStatus()+"   "+msgCheckDTO.getDesc());
    }
}
