package cn.junbao.yubao.live.user.provider.dao.mapper;

import cn.junbao.yubao.live.user.provider.dao.po.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IUserMapper {
    UserPO getUserById(Long userId);

    List<UserPO> getBatchByUserIds(@Param("userIds") List<Long> userIds);
}
