package cn.junbao.yubao.live.im.core.server.handler;

import cn.junbao.yubao.live.im.core.server.common.ImMsg;
import cn.junbao.yubao.live.im.core.server.handler.factory.ImHandlerFactory;
import cn.junbao.yubao.live.im.core.server.handler.factory.impl.ImHandlerFactoryImpl;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ChannelHandler.Sharable
public class ImServerCoreHandler extends SimpleChannelInboundHandler {
    @Resource
    private ImHandlerFactory imHandlerFactory ;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {

        if (! (msg instanceof ImMsg)){
            //数据类型错误
            log.error("[ImServerCoreHandler] 数据类型错误,msg is {}",msg);
            return;
        }
        ImMsg imMsg = (ImMsg) msg;
        //进行消息处理
        imHandlerFactory.doMsgHandler(channelHandlerContext,imMsg);
    }
}
