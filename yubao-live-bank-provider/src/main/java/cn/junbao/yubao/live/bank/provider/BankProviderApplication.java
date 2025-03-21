package cn.junbao.yubao.live.bank.provider;

import cn.junbao.yubao.live.bank.constants.OrderStatusEnum;
import cn.junbao.yubao.live.bank.dto.PayOrderDTO;
import cn.junbao.yubao.live.bank.provider.service.ICurrencyAccountService;
import cn.junbao.yubao.live.bank.provider.service.IPayOrderService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: Junbao
 * @Date: 2025/3/7 22:31
 * @Description:
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
@EnableScheduling
public class BankProviderApplication implements CommandLineRunner {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        SpringApplication springApplication = new SpringApplication(BankProviderApplication.class);
        //springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
        countDownLatch.await();
    }


    @Resource
    private ICurrencyAccountService currencyAccountService;
    @Resource
    private IPayOrderService payOrderService;
    @Override
    public void run(String... args) throws Exception {
        Integer balance = currencyAccountService.getBalance(39213L);
        System.out.println("===========balance============"+balance);

        PayOrderDTO payOrderDTO = new PayOrderDTO();
        payOrderDTO.setProductId(101);
        payOrderDTO.setOrderId(ThreadLocalRandom.current().nextLong());
        payOrderDTO.setProductName("产品名称-====");
        payOrderDTO.setProductDesc("描述-====");
        payOrderDTO.setTotalAmount(999);
        payOrderDTO.setPayTime(new Date());
        payOrderDTO.setStatus(OrderStatusEnum.WAITING_PAY.getCode());
        //payOrderService.doPrepayOrder(payOrderDTO);

        System.out.println("========payurl ========"+payOrderDTO.getPayUrl());

    }
}
