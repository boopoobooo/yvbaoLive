package cn.junbao.yubao.live.msg.provider.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云短信服务配置类
 */
@Configuration
@ConfigurationProperties(prefix = "aliyun.sms")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AliyunSmsProperties {
    private String accessKeyId;
    private String accessKeySecret;
    private String regionId;
    private String phoneNumbers;
    private String signName;
    private String templateCode;
}
