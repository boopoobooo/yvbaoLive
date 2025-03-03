package cn.junbao.yubao.live.common.interfaces.topic;

public class ImCoreServerTopicNames {
    public static final String  YUBAO_IM_BIZ_MSG_TOPIC = "yubao_im_biz_msg_topic";
    public static final String  YUBAO_IM_MSG_ACK_TOPIC = "yubao_im_ack_msg_topic";
    /**
     * 用户初次登录im服务发送mq
     */
    public static final String IM_ONLINE_TOPIC = "im_online_topic";

    /**
     * 用户断开im服务发送mq
     */
    public static final String IM_OFFLINE_TOPIC = "im_offline_topic";
}
