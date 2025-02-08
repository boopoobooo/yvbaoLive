package cn.junbao.yubao.live.user.provider.dao.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户标签 持久化对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTagPO {
    private Long id;
    private Long userId;
    private Long tagInfo01;
    private Long tagInfo02;
    private Long tagInfo03;
    private Date createTime;
    private Date updateTime;
}
