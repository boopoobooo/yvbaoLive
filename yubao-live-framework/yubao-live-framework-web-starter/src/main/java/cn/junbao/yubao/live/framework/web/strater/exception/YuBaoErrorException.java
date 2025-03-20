package cn.junbao.yubao.live.framework.web.strater.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;

/**
 * 系统异常定义
 */
@Getter
@AllArgsConstructor
public class YuBaoErrorException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 5248478286893724763L;

    private int errorCode;
    private String errorMsg;


}
