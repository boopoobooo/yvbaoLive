package cn.junbao.yubao.live.user.provider.dao.mapper;

import cn.junbao.yubao.live.user.provider.dao.po.UserTagPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IUserTagMapper {
    /**
     * 设置tag位置的标签
     * 通过或运算  保证第一个设置成功
     */
    int setTag(@Param("userId") Long userId, @Param("fieldName") String fieldName, @Param("tag") long tag);

    /**
     * 取消位置上的标签
     */
    int cancelTag(@Param("userId") Long userId,@Param("fieldName") String fieldName,@Param("tag") long tag);

    UserTagPO selectByUserId(Long userId);

    void insert(Long userId);
}
