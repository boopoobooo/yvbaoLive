/**
 * delete by junbao   0209 2025
 * shardingjdbc升级到 5.5.1  移除了ShardingSphereURLProvider类
 */
/*
package cn.junbao.yubao.live.framework.datasource.starter.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.driver.jdbc.core.driver.ShardingSphereURLProvider;

import java.util.Properties;

@Slf4j
public class NacosDriverURLProvider implements ShardingSphereURLProvider {

    private static final String NACOS_TYPE = "nacos:";
    private static final String GROUP = "DEFAULT_GROUP";
    @Override
    public boolean accept(String url) {
        return !Strings.isNullOrEmpty(url)&& url.contains(NACOS_TYPE);
    }

*
     * 从url中获取到nacos的连接配置信息
     *
     * url=jdbc:shardingsphere:nacos:localhost:8848/yubao-live-user-shardingjdbc.yaml?username=nacos&&password=nacos&&namespace=public
     * urlPrefix= jdbc:shardingsphere:


    @Override
    public byte[] getContent(String url, String urlPrefix) {
        if (Strings.isNullOrEmpty(url)) {
            return null;
        }
        //url: jdbc:shardingsphere:nacos://yubao.nacos.server:8848/yubao-live-user-shardingjdbc.yaml?username=nacos&&password=nacos&&namespace=public
        // 解析 URL
        //得到localhost:8848/yubao-live-user-shardingjdbc.yaml?username=nacos&&password=nacos&&namespace=public
        String nacosUrl = url.substring(urlPrefix.length() + NACOS_TYPE.length());
        String[] parts = nacosUrl.split("\\?");

        //  localhost:8848/yubao-live-user-shardingjdbc.yaml
        String configPath = parts[0];

        String[] configPathParts = configPath.split("/");
        String serverAddr = configPathParts[0];//yubao.nacos.server:8848
        String dataId = configPathParts[1];// /yubao-live-user-shardingjdbc.yaml

        // 解析查询参数
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR,serverAddr);

        if (parts.length == 2 ){
            //  username=nacos&&password=nacos&&namespace=public
            String[] acceptProp = acceptProp = parts[1].split("&&");
            for (String propertyName  : acceptProp) {
                String[] propertyItem = propertyName.split("=");
                String key = propertyItem[0];
                String value = propertyItem[1];
                if ("username".equals(key)) {
                    properties.setProperty(PropertyKeyConst.USERNAME, value);
                } else if ("password".equals(key)) {
                    properties.setProperty(PropertyKeyConst.PASSWORD, value);
                } else if ("namespace".equals(key)) {
                    properties.setProperty(PropertyKeyConst.NAMESPACE, value);
                }
            }//for

        }

        try {
            // 创建 Nacos ConfigService
            ConfigService configService = NacosFactory.createConfigService(properties);
            // 从 Nacos 获取配置内容
            String content = configService.getConfig(dataId, GROUP, 6000);
            log.info("Nacos content = {}",content);
            return content.getBytes();
        } catch (NacosException e) {
            throw new RuntimeException("Failed to get configuration from Nacos", e);
        }


    }///

public static void main(String[] args) throws NacosException {
        //初始化配置中心的Nacos Java SDK
        String serverAddr = "localhost:8848";
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR,serverAddr);
        properties.setProperty(PropertyKeyConst.USERNAME,"nacos");
        properties.setProperty(PropertyKeyConst.PASSWORD,"nacos");
        properties.setProperty(PropertyKeyConst.NAMESPACE,"public");

        ConfigService configService = NacosFactory.createConfigService(properties);
        String config = configService.getConfig("yubao-live-user-shardingjdbc.yaml", GROUP, 6000);
        System.out.println(config);
    }

}
*/
