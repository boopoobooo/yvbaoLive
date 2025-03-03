package cn.junbao.yubao.live.api.controller;

import cn.junbao.yubao.live.api.service.IHomePageService;
import cn.junbao.yubao.live.api.vo.HomePageVO;
import cn.junbao.yubao.live.common.interfaces.vo.WebResponseVO;
import cn.junbao.yubao.live.framework.web.strater.context.WebRequestContext;
import com.alibaba.cloud.commons.lang.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
@Slf4j
public class HomePageController {

    @Resource
    private IHomePageService homePageService;

    /**
     * 初始化首页信息
     */
    @PostMapping("/initPage")
    public WebResponseVO initPage() {
        Long userId = WebRequestContext.getUserId() ;
        log.info("[initPage] WebRequestContext.getUserId() = {}",userId);
        HomePageVO homePageVO = new HomePageVO();
        homePageVO.setLoginStatus(false);
        if (userId != null) {
            homePageVO = homePageService.initPage(userId);
            homePageVO.setLoginStatus(true);
        }
        return WebResponseVO.success(homePageVO);
    }
}
