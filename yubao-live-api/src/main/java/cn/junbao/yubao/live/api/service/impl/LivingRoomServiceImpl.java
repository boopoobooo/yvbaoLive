package cn.junbao.yubao.live.api.service.impl;

import cn.junbao.yubao.live.api.service.ILivingRoomService;
import cn.junbao.yubao.live.api.vo.LivingRoomInitVO;
import cn.junbao.yubao.live.api.vo.req.LivingRoomReqVO;
import cn.junbao.yubao.live.api.vo.req.OnlinePKReqVO;
import cn.junbao.yubao.live.api.vo.resp.RedPacketReceiveRespVO;
import cn.junbao.yubao.live.framework.web.strater.context.WebRequestContext;
import cn.junbao.yubao.live.gift.dto.RedPacketConfigReqDTO;
import cn.junbao.yubao.live.gift.dto.RedPacketConfigRespDTO;
import cn.junbao.yubao.live.gift.dto.RedPacketReceiveDTO;
import cn.junbao.yubao.live.gift.interfaces.IGiftConfigRpc;
import cn.junbao.yubao.live.gift.interfaces.IRedPacketConfigRpc;
import cn.junbao.yubao.live.im.constants.AppIdEnum;
import cn.junbao.yubao.live.living.interfaces.dto.LivingPkRespDTO;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomReqDTO;
import cn.junbao.yubao.live.living.interfaces.dto.LivingRoomRespDTO;
import cn.junbao.yubao.live.living.interfaces.rpc.ILivingRoomRpc;
import cn.junbao.yubao.live.user.dto.UserDTO;
import cn.junbao.yubao.live.user.interfaces.IUserRpc;
import com.alibaba.cloud.commons.lang.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
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
    @DubboReference(check = false)
    private IRedPacketConfigRpc redPacketConfigRpc;



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
    public List<LivingRoomRespDTO> list(LivingRoomReqVO livingRoomReqVO) {
        Integer type = livingRoomReqVO.getType();
        int pageNum = livingRoomReqVO.getPageNum();
        int pageSize = livingRoomReqVO.getPageSize();
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
        respVO.setAvatar(StringUtils.isEmpty(anchor.getAvatar()) ? "/img/4.jpeg" : anchor.getAvatar());

        // 设置当前用户的头像
        respVO.setWatcherAvatar(watcher.getAvatar());

        // 检查直播间是否存在，如果不存在则设置roomId为-1
        if (livingRoomRespDTO == null || livingRoomRespDTO.getAnchorId() == null || userId == null) {
            respVO.setRoomId(-1);
            return respVO;
        }

        boolean isAuthor = livingRoomRespDTO.getAnchorId().equals(userId);
        respVO.setRoomId(livingRoomRespDTO.getRoomId());
        respVO.setAnchorId(livingRoomRespDTO.getAnchorId());
        respVO.setAnchor(isAuthor);
        //配置红包雨code
        if (isAuthor) {
            RedPacketConfigRespDTO redPacketConfigRespDTO = redPacketConfigRpc.queryByAnchorId(userId);
            if (redPacketConfigRespDTO != null) {
                respVO.setRedPacketConfigCode(redPacketConfigRespDTO.getConfigCode());
            }
        }

        else {
            // 如果直播间存在，设置直播间ID和主播ID
            respVO.setRoomId(livingRoomRespDTO.getId());
            respVO.setAnchorId(livingRoomRespDTO.getAnchorId());
            // 判断当前用户是否是主播
            respVO.setAnchor(livingRoomRespDTO.getAnchorId().equals(userId));
        }

        respVO.setDefaultBgImg("/img/4.jpeg");
        return respVO;

    }

    //LivingRoomServiceImpl
    @Override
    public boolean onlinePK(OnlinePKReqVO onlinePKReqVO) {
        LivingRoomReqDTO reqDTO = new LivingRoomReqDTO();
        reqDTO.setRoomId(onlinePKReqVO.getRoomId());
        reqDTO.setAppId(AppIdEnum.YUBAO_LIVE_BIZ.getAppId());
        reqDTO.setPkObjId(WebRequestContext.getUserId());
        LivingPkRespDTO livingPkRespDTO = livingRoomRpc.onlinePK(reqDTO);
        if (!livingPkRespDTO.isOnlineStatus()){
            log.warn("[onlinePK] 开播失败...LivingPkRespDTO错误消息 = {}",livingPkRespDTO.getMsg());
            return false;
        }
        return true;
    }

    @Override
    public Boolean prepareRedPacket(Long userId, Integer roomId) {
        LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByRoomId(roomId);
        if (livingRoomRespDTO == null){
            log.warn("[prepareRedPacket] 直播间不存在...roomId = {}",roomId);
            return false;
        }
        if (livingRoomRespDTO.getAnchorId().equals(userId)){
            log.warn("[prepareRedPacket] 只能直播间主播才能初始化红包雨...roomId = {}",roomId);
        }
        return redPacketConfigRpc.prepareRedPacket(userId);
    }

    @Override
    public Boolean startRedPacket(Long userId, String code) {
        log.info("[startRedPacket] 开始红包雨...userId = {}, code = {}",userId,code);
        RedPacketConfigReqDTO reqDTO = new RedPacketConfigReqDTO();
        reqDTO.setUserId(userId);
        reqDTO.setRedPacketConfigCode(code);
        LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByAnchorId(userId);
        reqDTO.setRoomId(livingRoomRespDTO.getRoomId());
        return redPacketConfigRpc.startRedPacket(reqDTO);
    }

    @Override
    public RedPacketReceiveRespVO getRedPacket(Long userId, String redPacketConfigCode) {
        log.info("[getRedPacket] 领取红包...userId = {}, redPacketConfigCode = {}",userId,redPacketConfigCode);
        RedPacketConfigReqDTO reqDTO = new RedPacketConfigReqDTO();
        reqDTO.setUserId(userId);
        reqDTO.setRedPacketConfigCode(redPacketConfigCode);
        RedPacketReceiveDTO redPacketReceiveDTO = redPacketConfigRpc.receiveRedPacket(reqDTO);
        RedPacketReceiveRespVO redPacketReceiveRespVO = new RedPacketReceiveRespVO();
        if (redPacketReceiveDTO == null) {
            redPacketReceiveRespVO.setMsg("红包已派发完毕");
        } else {
            redPacketReceiveRespVO.setPrice(redPacketReceiveDTO.getPrice());
            redPacketReceiveRespVO.setMsg(redPacketReceiveDTO.getNotifyMsg());
        }
        return redPacketReceiveRespVO;
    }
}
