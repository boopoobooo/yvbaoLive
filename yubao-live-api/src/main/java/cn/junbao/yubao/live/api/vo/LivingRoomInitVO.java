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
    private String watcherNickName;
    //观众头像
    private String watcherAvatar;

    //房间ID
    private Integer roomId;
    //主播昵称
    private String anchorNickName;
    private String anchorImg;
    private String roomName;
    private String avatar;
    //红包雨的配置校验code
    private String redPacketConfigCode;
    /**
     * 当前用户是否为当前房间主播
     */
    private boolean isAnchor;

    //默认的 背景图片
    private String defaultBgImg;




}
