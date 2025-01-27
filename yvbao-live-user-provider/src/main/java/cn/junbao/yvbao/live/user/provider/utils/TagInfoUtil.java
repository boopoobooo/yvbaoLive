package cn.junbao.yvbao.live.user.provider.utils;

public class TagInfoUtil {
    public static boolean isContainTag(Long tagInfo,Long matchTag){
        return tagInfo != null && matchTag != null && matchTag > 0 && (tagInfo & matchTag) == matchTag;
    }

}
