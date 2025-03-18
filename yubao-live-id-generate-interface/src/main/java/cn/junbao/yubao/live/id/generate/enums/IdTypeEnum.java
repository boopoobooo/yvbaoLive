package cn.junbao.yubao.live.id.generate.enums;

public enum IdTypeEnum {

    USER_ID(1,"用户id生成策略"),
    ROOM_ID(3,"房间id生成策略");

    int code;
    String desc;

    IdTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
