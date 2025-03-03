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

    private Integer roomId;
    private Long anchorId;
    private Long userId;
    private String anchorImg;
    private String roomName;
    private String avatar;
    /**
     * 当前用户是否为当前房间主播
     */
    private boolean isAnchor;
    private String watcherNickName;
    //主播头像
    private String anchorNickName;
    //观众头像
    private String watcherAvatar;
    //默认的 背景图片
    private String defaultBgImg;


}
