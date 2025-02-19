package cn.junbao.yubao.live.im.core.server.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 消息编码器
 */
public class ImMsgEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf byteBuf) throws Exception {
        ImMsg imMsg= (ImMsg) msg;
        byteBuf.writeShort(imMsg.getMagic());
        byteBuf.writeInt(imMsg.getMsgCode());
        byteBuf.writeInt(imMsg.getLength());
        byteBuf.writeBytes(imMsg.getBody());
    }
}
