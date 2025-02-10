package cn.junbao.yubao.live.user.provider;

import cn.junbao.yubao.live.user.constants.UserTagsEnum;
import cn.junbao.yubao.live.user.dto.UserLoginDTO;
import cn.junbao.yubao.live.user.provider.service.IUserPhoneService;
import cn.junbao.yubao.live.user.provider.service.IUserTagService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@EnableDubbo
public class UserProviderApplication implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(UserProviderApplication.class);

    @Resource
    private IUserTagService userTagService;
    @Resource
    private IUserPhoneService userPhoneService;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(UserProviderApplication.class);
        springApplication.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        String key = "test001";
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUserId(100001L);
        redisTemplate.opsForValue().set(key,userLoginDTO);
        System.out.println("==================================");
        UserLoginDTO obj = (UserLoginDTO) redisTemplate.opsForValue().get(key);
        System.out.println("userId === "+obj.getUserId());
        System.out.println("===============redis____test___====="+redisTemplate.opsForValue().get(key));


        String phone = "15014988572";
        UserLoginDTO loginDTO = userPhoneService.login(phone);
        System.out.println("================loginDTO======================"+loginDTO);

        System.out.println(userPhoneService.queryByPhone(phone));
        System.out.println(userPhoneService.queryByPhone(phone));
        System.out.println(userPhoneService.queryByUserId(loginDTO.getUserId()));
        System.out.println(userPhoneService.queryByUserId(loginDTO.getUserId()));
    }



   /* @Override
    public void run(String... args) throws Exception {
        long userId = 10041l;
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
    }*/
}
