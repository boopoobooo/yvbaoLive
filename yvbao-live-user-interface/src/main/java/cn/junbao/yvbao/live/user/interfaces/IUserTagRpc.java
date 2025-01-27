package cn.junbao.yvbao.live.user.interfaces;

import cn.junbao.yvbao.live.user.constants.UserTagsEnum;

public interface IUserTagRpc {
    boolean setTag(Long userId , UserTagsEnum userTagsEnum);

    boolean cancelTag(Long userId , UserTagsEnum userTagsEnum);

    boolean containTag(Long userId , UserTagsEnum userTagsEnum);
}
