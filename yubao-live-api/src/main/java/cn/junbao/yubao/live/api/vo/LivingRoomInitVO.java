package cn.junbao.yubao.live.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LivingRoomInitVO {

    private Long anchorId;
    private Long userId;
    private String anchorImg;
    private String roomName;
    private String avatar;
    private Integer roomId;
    private String watcherNickName;
    //主播头像
    private String anchorNickName;
    //观众头像
    private String watcherAvatar;


}
