package cn.junbao.yubao.live.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "yubao.gateway")
@Configuration
@RefreshScope
public class YuBaoGatewayProperties {
    private List<String> whileList;
}
