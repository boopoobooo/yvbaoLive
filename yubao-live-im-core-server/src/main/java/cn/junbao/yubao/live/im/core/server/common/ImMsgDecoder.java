package cn.junbao.yubao.live.im.core.server.common;

import cn.junbao.yubao.live.im.constants.ImConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ImMsgDecoder extends ByteToMessageDecoder {

    private final int BASE_LEN = 2 + 4 + 4 ;
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //byteBuf的基础校验: 长度校验，magic值校验
        if (byteBuf.readableBytes() >= BASE_LEN){
            //符合基础长度
            if (byteBuf.readShort() != ImConstants.DEFAULT_MAGIC){
                channelHandlerContext.close();
                return;
            }
            int code = byteBuf.readInt();
            int length = byteBuf.readInt();
            if (byteBuf.readableBytes() < length){
                //消息不完整
                channelHandlerContext.close();
                return;
            }
            byte[] body = new byte[length];
            byteBuf.readBytes(body);
            //转换为imMsg对象
            ImMsg imMsg = new ImMsg();
            imMsg.setMagic(ImConstants.DEFAULT_MAGIC);
            imMsg.setMsgCode(code);
            imMsg.setLength(length);
            imMsg.setBody(body);
            list.add(imMsg);
        }

    }
}
