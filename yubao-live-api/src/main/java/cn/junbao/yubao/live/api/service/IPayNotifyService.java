package cn.junbao.yubao.live.api.service;

import java.util.Map;

public interface IPayNotifyService {
    boolean notifyHandler(Map<String, String> requestParams);
}
