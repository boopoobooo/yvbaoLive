package cn.junbao.yubao.live.im.core.server.starter;

import cn.junbao.yubao.im.core.server.constants.ImCoreServerConstants;
import cn.junbao.yubao.live.im.constants.ImConstants;
import cn.junbao.yubao.live.im.core.server.common.ChannelHandlerContextCache;
import cn.junbao.yubao.live.im.core.server.common.ImMsgDecoder;
import cn.junbao.yubao.live.im.core.server.common.ImMsgEncoder;
import cn.junbao.yubao.live.im.core.server.handler.ImServerCoreHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@Getter
@Setter
public class NettyImServerApplication implements InitializingBean {
    //指定端口
    private int port;
    @Value("${spring.application.registryIp}")
    private String registryIp;
    @Value("${spring.application.registryPort}")
    private String registryPort;

    @Resource
    private ImServerCoreHandler imServerCoreHandler;

    @Resource
    private Environment environment;

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    //基于netty启动java进程
    public void startApplication(int port) throws InterruptedException {
        setPort(port);
        //处理accept
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //处理read && write
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup,workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        //netty初始化相关handler
        bootstrap.childHandler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                log.info("[NettyImServerApplication] 初始化连接渠道");
                //添加消息编解码器
                channel.pipeline().addLast(new ImMsgEncoder());
                channel.pipeline().addLast(new ImMsgDecoder());
                //添加消息处理器
                channel.pipeline().addLast(imServerCoreHandler);
            }
        });
        //获取im的服务注册ip和暴露端口
        /*String registryIp = environment.getProperty("DUBBO_IP_TO_REGISTRY");
        String registryPort = environment.getProperty("DUBBO_PORT_TO_REGISTRY");*/
        if (StringUtils.isEmpty(registryPort) || StringUtils.isEmpty(registryIp)) {
            throw new IllegalArgumentException("启动参数中的注册端口和注册ip不能为空");
        }
        //加入到本地缓存中
        ChannelHandlerContextCache.setServerIpAddress(registryIp+":"+registryPort);

        //基于jvm钩子函数实现优雅关闭效果
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }));
        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        log.info("启动成功，监听端口：{}",getPort());
        //阻塞主线程实现服务长时间启动效果
        channelFuture.channel().closeFuture().sync();

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread nettyImServerThread = new Thread(()->{
            try {
                startApplication(9090);
            } catch (InterruptedException e) {
                log.error("ERROR: "+ e);
                throw new RuntimeException(e);
            }
        });
        nettyImServerThread.setName("yubao-live-im-core-NettyCoreThread");
        nettyImServerThread.start();
    }
}
