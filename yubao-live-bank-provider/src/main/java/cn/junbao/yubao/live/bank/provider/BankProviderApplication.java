package cn.junbao.yubao.live.bank.provider;

import cn.junbao.yubao.live.bank.provider.service.ICurrencyAccountService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.concurrent.CountDownLatch;

/**
 * @Author: Junbao
 * @Date: 2025/3/7 22:31
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
public class BankProviderApplication implements CommandLineRunner {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        SpringApplication springApplication = new SpringApplication(BankProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
        countDownLatch.await();
    }


    @Resource
    private ICurrencyAccountService currencyAccountService;
    @Override
    public void run(String... args) throws Exception {
        Integer balance = currencyAccountService.getBalance(39213L);
        System.out.println("===========balance============"+balance);
    }
}
