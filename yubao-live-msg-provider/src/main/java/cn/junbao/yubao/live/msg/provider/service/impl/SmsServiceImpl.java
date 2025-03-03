package cn.junbao.yubao.live.msg.provider.service.impl;

import cn.junbao.yubao.live.framework.redis.starter.key.MsgProviderCacheKeyBuilder;
import cn.junbao.yubao.live.msg.dto.MsgCheckDTO;
import cn.junbao.yubao.live.msg.enums.MsgSendResultEnum;
import cn.junbao.yubao.live.msg.provider.config.AliyunSmsProperties;
import cn.junbao.yubao.live.msg.provider.dao.mapper.ISmsMapper;
import cn.junbao.yubao.live.msg.provider.dao.po.SmsPO;
import cn.junbao.yubao.live.msg.provider.service.ISmsService;
import cn.junbao.yubao.live.msg.provider.utils.AliyunSmsUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SmsServiceImpl implements ISmsService {
    @Resource
    private ISmsMapper smsMapper;

    @Autowired
    private MsgProviderCacheKeyBuilder msgProviderCacheKeyBuilder;
    @Resource
    private AliyunSmsProperties aliyunSmsProperties;
    @Resource
    private RedisTemplate<String,String> redisTemplate;
    @Resource
    private AliyunSmsUtil aliyunSmsUtil;

    @Override
    public MsgSendResultEnum sendLoginCode(String phone) {
        log.info("[sendLoginCode] phone:{}",phone);
        //1. 基础校验
        if (StringUtils.isBlank(phone)){
            return MsgSendResultEnum.MSG_PARAM_ERROR;
        }
        //2. 校验手机号是否频繁验证
        String cacheKey = msgProviderCacheKeyBuilder.buildSmsLoginKey(phone);
        Boolean hasKey = null;
        try {
            hasKey = redisTemplate.hasKey(cacheKey);
        } catch (Exception e) {
            log.error("[ERROR] e = "+e);
            throw new RuntimeException(e);
        }
        if (hasKey){
            log.warn("[sendLoginCode] 当前手机号验证频繁，phone = {}",phone);
            return MsgSendResultEnum.SEND_FAIL;
        }
        //3. 生成验证码
        String loginCode = RandomStringUtils.randomNumeric(6);
        redisTemplate.opsForValue().set(cacheKey,loginCode,60, TimeUnit.SECONDS);
        log.info("[sendLoginCode] 本次验证码 =  {}",redisTemplate.opsForValue().get(cacheKey));

        try {
            //发送验证码
            //sendMockSms(phone,loginCode);
            boolean sendSmsStatus = aliyunSmsUtil.sendAliyunSms(aliyunSmsProperties.getTemplateCode(), phone, loginCode);
            //插入数据库
            if (sendSmsStatus){
                insertOne(phone,loginCode);//插入短信记录
            }
        } catch (Exception e) {
            log.error("[sendLoginCode]ERROR:"+e);
            throw new RuntimeException(e);
        }
        return MsgSendResultEnum.SEND_SUCCESS;
    }

    @Override
    public MsgCheckDTO checkLoginCode(String phone, String code) {
        log.info("[checkLoginCode] phone = {},code = {}",phone,code);
        if (StringUtils.isBlank(phone)||code == null || code.length() < 6){
            return new MsgCheckDTO(false,MsgSendResultEnum.MSG_PARAM_ERROR.getDesc());
        }

        String cacheKey = msgProviderCacheKeyBuilder.buildSmsLoginKey(phone);
        String cacheLoginCode = redisTemplate.opsForValue().get(cacheKey);
        if (cacheLoginCode == null ) {
            log.warn("校验验证码失败， 当前手机号验证码已过期:{},code ={} ",phone,code);
            return new MsgCheckDTO(false,"当前手机号验证码已过期");
        }
        if (cacheLoginCode.equals(code)){
            log.info("[checkLoginCode]校验验证码成功");
            return new MsgCheckDTO(true,"验证成功");
        }

        return new MsgCheckDTO(false,"验证失败");
    }

    /**
     * 模拟的短信服务， 后续将进行 第三方短信对接
     *  @Deprecated： 完成aliyun-sms服务对接， 使用aliyunSmsUtil
     */
    @Deprecated
    private boolean sendMockSms(String phone,String loginCode){
        //模拟发送短信服务
        try {
            log.info(" ============= 创建短信发送通道中 ============= ,phone is {},code is {}", phone, loginCode);
            Thread.sleep(1000);
            log.info(" ============= 短信已经发送成功 ============= ");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return true;
    }


    private void insertOne(String phone, String  loginCode){
        SmsPO smsPO = new SmsPO();
        smsPO.setPhone(phone);
        smsPO.setCode(loginCode);
        smsMapper.insertOne(smsPO);
    }
}
