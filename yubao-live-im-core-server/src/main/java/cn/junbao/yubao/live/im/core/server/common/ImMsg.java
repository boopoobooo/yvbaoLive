package cn.junbao.yubao.live.im.core.server.common;

import cn.junbao.yubao.live.im.constants.ImConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImMsg {
    //
    private short  magic;
    //标识当前消息
    private int code;
    //消息体长度
    private int length;
    //存储消息体的内容
    private byte[] body;

    public static ImMsg build(int code , String body){
        ImMsg imMsg = new ImMsg();
        imMsg.setMagic(ImConstants.DEFAULT_MAGIC);
        imMsg.setCode(code);
        imMsg.setBody(body.getBytes());
        imMsg.setLength(body.getBytes().length);
        return imMsg;
    }

    @Override
    public String toString() {
        return "ImMsg{" +
                "magic=" + magic +
                ", code=" + code +
                ", length=" + length +
                ", body=" + new String(body) +
                '}';
    }
}
