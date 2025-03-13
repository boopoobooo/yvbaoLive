package cn.junbao.yubao.live.api.service.impl;

import cn.junbao.yubao.live.api.service.ILivingRoomService;
import cn.junbao.yubao.live.api.vo.LivingRoomInitVO;
import cn.junbao.yubao.live.api.vo.LivingRoomPageRespVO;
import cn.junbao.yubao.live.framework.web.strater.context.WebRequestContext;
import cn.junbao.yubao.live.im.constants.AppIdEnum;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomReqDTO;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomRespDTO;
import cn.junbao.yubao.live.living.interfaces.rpc.ILivingRoomRpc;
import cn.junbao.yubao.live.user.dto.UserDTO;
import cn.junbao.yubao.live.user.interfaces.IUserRpc;
import com.alibaba.cloud.commons.lang.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: Junbao
 * @Date: 2025/2/22 23:55
 * @Description:
 */
@Service
@Slf4j
public class LivingRoomServiceImpl implements ILivingRoomService {
    @DubboReference(check = false)
    private ILivingRoomRpc livingRoomRpc;

    @DubboReference(check = false)
    private IUserRpc userRpc;


    /**
     * 直播间开播
     */
    @Override
    public Integer startLiving(Integer type) {
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setAnchorId(WebRequestContext.getUserId());
        livingRoomReqDTO.setRoomName("主播:"+WebRequestContext.getUserId()+"的房间");
        livingRoomReqDTO.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
        livingRoomReqDTO.setType(type);
        Integer livingRoomId = livingRoomRpc.startLivingRoom(livingRoomReqDTO);

        return livingRoomId;
    }

    @Override
    public boolean closeLiving(Integer roomId) {
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setAnchorId(WebRequestContext.getUserId());
        livingRoomReqDTO.setRoomId(roomId);
        return livingRoomRpc.closeLiving(livingRoomReqDTO );
    }

    @Override
    public List<LivingRoomRespDTO> list(Integer type,int pageNum, int pageSize) {
        List<LivingRoomRespDTO> livingRoomRespDTOList = livingRoomRpc.list(type,pageNum,pageSize);
        return livingRoomRespDTOList;
    }

    @Override
    public LivingRoomInitVO anchorConfig(Long userId, Integer roomId) {
        log.info("[anchorConfig] userId = {},roomId = {}",userId,roomId);
        // 调用RPC接口，根据roomId查询直播间信息
        LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByRoomId(roomId);

        if (livingRoomRespDTO == null){
            log.warn("[anchorConfig] livingRoomRespDTO is null, userId = {},roomId = {}",userId,roomId);
            return null;
        }
        // 查询主播和当前用户的用户信息
        Map<Long, UserDTO> userDTOMap = userRpc.batchQueryUserInfo(
                Arrays.asList(livingRoomRespDTO.getAnchorId(), userId).stream().distinct().collect(Collectors.toList())
        );

        UserDTO anchor = userDTOMap.get(livingRoomRespDTO.getAnchorId());
        UserDTO watcher = userDTOMap.get(userId);
        // 创建返回的VO对象
        LivingRoomInitVO respVO = new LivingRoomInitVO();

        respVO.setAnchorNickName(anchor.getNickName());
        respVO.setWatcherNickName(watcher.getNickName());
        respVO.setUserId(userId);
        respVO.setAvatar(StringUtils.isEmpty(anchor.getAvatar()) ? "https://s1.ax1x.com/2022/12/18/zb6q6f.png" : anchor.getAvatar());

        // 设置当前用户的头像
        respVO.setWatcherAvatar(watcher.getAvatar());

        // 检查直播间是否存在，如果不存在则设置roomId为-1
        if (livingRoomRespDTO == null || livingRoomRespDTO.getAnchorId() == null || userId == null) {
            respVO.setRoomId(-1);
        } else {
            // 如果直播间存在，设置直播间ID和主播ID
            respVO.setRoomId(livingRoomRespDTO.getId());
            respVO.setAnchorId(livingRoomRespDTO.getAnchorId());
            // 判断当前用户是否是主播
            respVO.setAnchor(livingRoomRespDTO.getAnchorId().equals(userId));
        }

        // 设置默认背景图片
        respVO.setDefaultBgImg("https://picst.sunbangyan.cn/2023/08/29/waxzj0.png");
        return respVO;

    }
}
