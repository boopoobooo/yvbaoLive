package cn.junbao.yubao.live.bank.provider.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "alipay", ignoreInvalidFields = true)
public class AliPayConfigProperties {

    private String appId; // appID
    private String gatewayUrl;
    private String appPrivateKey; // 应用私钥
    private String alipayPublicKey; // 支付宝公钥
    private String notifyUrl; // 支付成功后支付宝异步通知的接口地址
    private String returnUrl; // 支付成功之后返回的路径

}
