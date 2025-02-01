package cn.junbao.yvbao.live.user.provider.dao.po;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPO {
    private Long id;
    private Long userId;
    private String nickName;
    private String trueName;
    private String avatar;
    private Integer gender;
    private Integer workCity;
    private Integer bornCity;
    private Date bornDate;
    private Date createTime;
    private Date updateTime;
}