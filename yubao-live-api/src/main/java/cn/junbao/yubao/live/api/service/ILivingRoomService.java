package cn.junbao.yubao.live.api.service;

public interface ILivingRoomService {

    Integer startLiving(Integer type);

    boolean closeLiving(Integer roomId);
}
