package cn.junbao.yubao.live.bank.provider.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.alipay.api.AlipayConstants.*;

@Configuration
@EnableConfigurationProperties(AliPayConfigProperties.class)
public class AliPayConfig {

    @Bean("alipayClient")
    public AlipayClient alipayClient(AliPayConfigProperties properties) {
        return new DefaultAlipayClient(
                properties.getGatewayUrl(),
                properties.getAppId(),
                properties.getAppPrivateKey(),
                FORMAT_JSON,
                CHARSET_UTF8,
                properties.getAlipayPublicKey(),
                SIGN_TYPE_RSA2
        );
    }


}
