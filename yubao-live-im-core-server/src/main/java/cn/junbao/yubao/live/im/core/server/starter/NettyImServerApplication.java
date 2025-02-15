package cn.junbao.yubao.live.im.core.server.starter;

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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class NettyImServerApplication implements InitializingBean {
    //指定端口
    private int port;

    @Resource
    private ImServerCoreHandler imServerCoreHandler;

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

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
                log.info("初始化连接渠道");
                //添加消息编解码器
                channel.pipeline().addLast(new ImMsgEncoder());
                channel.pipeline().addLast(new ImMsgDecoder());
                //添加消息处理器
                channel.pipeline().addLast(imServerCoreHandler);
            }
        });
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

   /* public static void main(String[] args) throws InterruptedException {
        NettyImServerApplication nettyImServerApplication = new NettyImServerApplication();
        nettyImServerApplication.startApplication(9090);
    }*/

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
