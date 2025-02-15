package cn.junbao.yubao.live.im.interfaces;

public interface ImTokenRpc {
    /**
     * 创建用户登录im服务的token
     * @param userId 用户id
     * @param appId  业务id
     * @return 生成的token
     */
    String createImLoginToken(long userId, int appId);

    /**
     * 根据token检索用户id
     * @param token 用户传入的token
     * @return token对应的用户id
     */
    Long getUserIdByToken(String token);
}
