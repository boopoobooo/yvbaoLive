package cn.junbao.yubao.live.gift.provider;

import cn.junbao.yubao.live.common.interfaces.enums.CommonStatusEum;
import cn.junbao.yubao.live.gift.dto.GiftConfigDTO;
import cn.junbao.yubao.live.gift.dto.GiftRecordDTO;
import cn.junbao.yubao.live.gift.provider.service.IGiftConfigService;
import cn.junbao.yubao.live.gift.provider.service.IGiftRecordService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: Junbao
 * @Date: 2025/3/6 21:49
 * @Description:
 */
@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class GiftProviderApplication implements CommandLineRunner {
    public static void main(String[] args)  throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        SpringApplication springApplication =new SpringApplication(GiftProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
        countDownLatch.await();
    }


    @Resource
    private IGiftConfigService giftConfigService;
    @Resource
    private IGiftRecordService giftRecordService;
    @Override
    public void run(String... args) throws Exception {
        List<GiftConfigDTO> giftConfigDTOS = giftConfigService.queryGiftList();
        for (GiftConfigDTO giftConfigDTO : giftConfigDTOS) {
            System.out.println(giftConfigDTO);
        }
    }
}
