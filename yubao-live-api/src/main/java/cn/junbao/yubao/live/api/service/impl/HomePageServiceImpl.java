package cn.junbao.yubao.live.api.service.impl;

import cn.junbao.yubao.live.api.service.IHomePageService;
import cn.junbao.yubao.live.api.vo.HomePageVO;
import cn.junbao.yubao.live.user.constants.UserTagsEnum;
import cn.junbao.yubao.live.user.dto.UserDTO;
import cn.junbao.yubao.live.user.interfaces.IUserRpc;
import cn.junbao.yubao.live.user.interfaces.IUserTagRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @Author idea
 * @Date: Created in 23:03 2023/7/19
 * @Description
 */
@Service
public class HomePageServiceImpl implements IHomePageService {

    @DubboReference(check = false)
    private IUserRpc userRpc;
    @DubboReference(check = false)
    private IUserTagRpc userTagRpc;

    @Override
    public HomePageVO initPage(Long userId) {
        UserDTO userDTO = userRpc.getUserById(userId);
        HomePageVO homePageVO = new HomePageVO();
        if (userDTO != null) {
            homePageVO.setAvatar(userDTO.getAvatar());
            homePageVO.setUserId(userId);
            homePageVO.setNickName(userDTO.getNickName());

            //vip用户有权利开播
            homePageVO.setShowStartLivingBtn(userTagRpc.containTag(userId, UserTagsEnum.IS_VIP_USER));
        }
        return homePageVO;
    }
}
