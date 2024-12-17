package cn.junbao.yvbao.live.user.provider.dao.mapper;

import cn.junbao.yvbao.live.user.provider.dao.po.UserPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserMapper {
    UserPO getUserById(Long userId);
}
