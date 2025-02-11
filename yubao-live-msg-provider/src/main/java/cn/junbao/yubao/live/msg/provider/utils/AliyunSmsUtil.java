package cn.junbao.yubao.live.msg.provider.utils;

import cn.junbao.yubao.live.msg.provider.config.AliyunSmsProperties;
import com.alibaba.fastjson2.JSONObject;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.google.gson.Gson;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class AliyunSmsUtil {

    private final  AsyncClient client;
    private String signName;

    public AliyunSmsUtil(AliyunSmsProperties aliyunSmsProperties) {
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(aliyunSmsProperties.getAccessKeyId())
                .accessKeySecret(aliyunSmsProperties.getAccessKeySecret())
                //.securityToken(System.getenv("ALIBABA_CLOUD_SECURITY_TOKEN")) // use STS token
                .build());

        // Configure the Client
        client = AsyncClient.builder()
                .region(aliyunSmsProperties.getRegionId()) // Region ID
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
                                .setEndpointOverride("dysmsapi.aliyuncs.com")
                        //.setConnectTimeout(Duration.ofSeconds(30))
                )
                .build();
        signName = aliyunSmsProperties.getSignName();
    }

    public boolean sendAliyunSms(String templateCode, String phoneNumber , String data){

        JSONObject templateParam = new JSONObject();
        templateParam.put("code", data);

        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .phoneNumbers(phoneNumber)
                .signName(signName)
                .templateCode(templateCode)
                .templateParam(templateParam.toString())
                // Request-level configuration rewrite, can set Http request parameters, etc.
                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                .build();
        try {
            // Asynchronously get the return value of the API request
            CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
            // Synchronously get the return value of the API request
            SendSmsResponse resp = response.get();
            log.info("[aliyunUtil]sendSms --response:{}",new Gson().toJson(resp));
            return resp.getBody().getBizId() != null; // 如果返回的BizId不为空，表示发送成功
            // Asynchronous processing of return values
            /*response.thenAccept(resp -> {
                log.info("[aliyun]sendSms --response:{}",new Gson().toJson(resp));
            }).exceptionally(throwable -> { // Handling exceptions
                log.error("[aliyun]sendSms ERROR --response:{}",new Gson().toJson(resp));
                return false;
            });*/

        } catch (Exception e) {
            log.error("[aliyun]sendSms ERROR --Exception:{}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void close() {
        client.close();
    }
}
