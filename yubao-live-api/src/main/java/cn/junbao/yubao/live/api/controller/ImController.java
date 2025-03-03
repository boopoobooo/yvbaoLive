package cn.junbao.yubao.live.api.controller;

import cn.junbao.yubao.live.api.service.ImService;
import cn.junbao.yubao.live.common.interfaces.vo.WebResponseVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Junbao
 * @Date: 2025/2/28 14:21
 * @Description:
 */
@RestController
@RequestMapping("/im")
@Slf4j
public class ImController {
    @Resource
    private ImService imService;

    @PostMapping("/getImConfig")
    public WebResponseVO getImConfig() {
        return WebResponseVO.success(imService.getImConfig());
    }
}
