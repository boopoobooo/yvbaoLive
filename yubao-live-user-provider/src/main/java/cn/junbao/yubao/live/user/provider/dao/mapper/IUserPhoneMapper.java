package cn.junbao.yubao.live.user.provider.dao.mapper;

import cn.junbao.yubao.live.user.dto.UserPhoneDTO;
import cn.junbao.yubao.live.user.provider.dao.po.UserPhonePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IUserPhoneMapper {
    UserPhonePO queryByPhone(String phone);

    void insertOne(UserPhonePO userPhonePO);

    List<UserPhonePO> queryByUserId(@Param("userId") Long userId);
}
