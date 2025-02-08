package cn.junbao.yubao.live.user.constants;

/**
 * 用户标签枚举类
 * 定义 tag_info 中字段中每一位对应的 标签类型
 */

public enum UserTagsEnum {
    IS_VIP_USER ((long) Math.pow(2,1),"是否为VIP用户","tag_info_01"),
    IS_OLD_USER ((long) Math.pow(2,2),"是否为老用户","tag_info_01");

    long tag;
    String desc;
    String fieldName;

    UserTagsEnum(long tag, String desc, String fieldName) {
        this.tag = tag;
        this.desc = desc;
        this.fieldName = fieldName;
    }

    public long getTag() {
        return tag;
    }

    public String getDesc() {
        return desc;
    }

    public String getFieldName() {
        return fieldName;
    }
}
