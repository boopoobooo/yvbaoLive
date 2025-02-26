package cn.junbao.yubao.live.msg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgCheckDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 342593415291809199L;

    private boolean checkStatus;
    private String desc;
}
