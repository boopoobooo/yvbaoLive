package cn.junbao.yubao.live.msg.provider.dao.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsPO {

    private Long id;
    private String phone;
    private String code;
    private Date sendTime;
    private Date updateTime;
}
