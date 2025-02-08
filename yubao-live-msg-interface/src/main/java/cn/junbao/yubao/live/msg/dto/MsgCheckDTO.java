package cn.junbao.yubao.live.msg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgCheckDTO implements Serializable {

    private boolean checkStatus;
    private String desc;
}
