package cn.junbao.yubao.live.id.generate;

import cn.junbao.yubao.live.id.generate.service.IdGenerateService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.HashSet;

@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
public class IdGenerateApplication implements CommandLineRunner{
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(IdGenerateApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);

        new Thread(
                ()->{
                    synchronized (IdGenerateApplication.class){
                        try {
                            IdGenerateApplication.class.wait();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
        ).start();
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("======================");
    }

    /*@Resource
    private IdGenerateService idGenerateService;
    @Override
    public void run(String... args) throws Exception {
        Long id = idGenerateService.getUnSeqId(1);
        System.out.println("================="+id);
    }*/
}
