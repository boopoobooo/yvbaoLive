package cn.junbao.yubao.live.common.interfaces.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebResponseVO {
    private int code;
    private String msg;
    private Object data;

    public static WebResponseVO bizError(String msg) {
        WebResponseVO webResponseVO = new WebResponseVO();
        webResponseVO.setCode(501);
        webResponseVO.setMsg(msg);
        return webResponseVO;
    }

    public static WebResponseVO bizError(Integer code, String msg) {
        WebResponseVO webResponseVO = new WebResponseVO();
        webResponseVO.setCode(code);
        webResponseVO.setMsg(msg);
        return webResponseVO;
    }



    public static WebResponseVO sysError() {
        WebResponseVO webResponseVO = new WebResponseVO();
        webResponseVO.setCode(500);
        return webResponseVO;
    }

    public static WebResponseVO sysError(String msg) {
        WebResponseVO webResponseVO = new WebResponseVO();
        webResponseVO.setCode(500);
        webResponseVO.setMsg(msg);
        return webResponseVO;
    }

    public static WebResponseVO errorParam() {
        WebResponseVO webResponseVO = new WebResponseVO();
        webResponseVO.setCode(400);
        webResponseVO.setMsg("error-param");
        return webResponseVO;
    }

    public static WebResponseVO errorParam(String msg) {
        WebResponseVO webResponseVO = new WebResponseVO();
        webResponseVO.setCode(401);
        webResponseVO.setMsg(msg);
        return webResponseVO;
    }

    public static WebResponseVO success() {
        WebResponseVO webResponseVO = new WebResponseVO();
        webResponseVO.setCode(200);
        webResponseVO.setMsg("success");
        webResponseVO.setData("success");
        return webResponseVO;
    }

    public static WebResponseVO success(Object data) {
        WebResponseVO webResponseVO = new WebResponseVO();
        webResponseVO.setData(data);
        webResponseVO.setCode(200);
        webResponseVO.setMsg("success");
        return webResponseVO;
    }
}
