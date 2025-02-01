package cn.junbao.yvbao.live.user.provider;

import cn.junbao.yvbao.live.user.constants.UserTagsEnum;
import cn.junbao.yvbao.live.user.provider.service.IUserTagService;
import groovy.util.logging.Slf4j;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@EnableDubbo
public class UserProviderApplication implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(UserProviderApplication.class);

    @Resource
    private IUserTagService userTagService;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(UserProviderApplication.class);
        springApplication.run(args);
    }


    @Override
    public void run(String... args) throws Exception {
        long userId = 10041l;
        /*System.out.println(userTagService.setTag(userId, UserTagsEnum.IS_VIP_USER));
        System.out.println(userTagService.containTag(userId,UserTagsEnum.IS_VIP_USER));
        System.out.println(userTagService.cancelTag(userId,UserTagsEnum.IS_VIP_USER));
        System.out.println(userTagService.containTag(userId,UserTagsEnum.IS_VIP_USER));*/

        userTagService.cancelTag(userId,UserTagsEnum.IS_VIP_USER);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        countDownLatch.await();
                        logger.info("========result is ===  "+userTagService.setTag(userId, UserTagsEnum.IS_VIP_USER));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // 恢复中断状态
                        logger.error("Thread [{}] was interrupted", Thread.currentThread().getName(), e);
                        throw new RuntimeException(e);
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }

        // 启动所有线程
        countDownLatch.countDown();

        // 等待所有子线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        logger.info("END ================");

        Thread.sleep(10000);
        System.out.println(userTagService.containTag(userId, UserTagsEnum.IS_VIP_USER));
    }
}
