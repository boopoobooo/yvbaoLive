package cn.junbao.yubao.live.user.provider.service.impl;

import cn.junbao.yubao.live.common.interfaces.enums.CommonStatusEum;
import cn.junbao.yubao.live.common.interfaces.utils.ConvertBeanUtils;
import cn.junbao.yubao.live.common.interfaces.utils.DESUtils;
import cn.junbao.yubao.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import cn.junbao.yubao.live.id.generate.enums.IdTypeEnum;
import cn.junbao.yubao.live.id.generate.interfaces.IdGenerateRpc;
import cn.junbao.yubao.live.user.dto.UserDTO;
import cn.junbao.yubao.live.user.dto.UserLoginDTO;
import cn.junbao.yubao.live.user.dto.UserPhoneDTO;
import cn.junbao.yubao.live.user.provider.dao.mapper.IUserMapper;
import cn.junbao.yubao.live.user.provider.dao.mapper.IUserPhoneMapper;
import cn.junbao.yubao.live.user.provider.dao.po.UserPO;
import cn.junbao.yubao.live.user.provider.dao.po.UserPhonePO;
import cn.junbao.yubao.live.user.provider.service.IUserPhoneService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserPhoneServiceImpl implements IUserPhoneService {
    @Resource
    private IUserPhoneMapper userPhoneMapper;
    @Resource
    private IUserMapper userMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;
    @DubboReference
    private IdGenerateRpc idGenerateRpc;

    @Override
    public UserLoginDTO login(String phone) {

        if (Strings.isBlank(phone)){
            log.warn("[login] phone is null");
            return null;
        }
        //是否注册
        UserPhoneDTO userPhoneDTO = this.queryByPhone(phone);
        //已注册，则 生成token，存入缓存，返回对象
        if (userPhoneDTO != null) {
            log.info("[login]用户已注册，登录成功");
            return UserLoginDTO.loginSuccess(userPhoneDTO.getUserId(),phone);
        }

        //未注册，创建对象， 插入数据库，生成token，存入缓存，返回
        log.info("[login]用户未注册,登录失败");
        return this.registerAndInsertDB(phone);
    }

    /**
     * 实现手机号用户的 注册 和 插入DB表
     * @param phone
     * @return
     */
    private UserLoginDTO registerAndInsertDB(String phone) {
        UserPO userPO = new UserPO();
        Long userId = idGenerateRpc.getUnSeqId(IdTypeEnum.USER_ID.getCode());
        userPO.setUserId(userId);
        userPO.setNickName("鱼宝用户-"+userId);
        userMapper.insertOne(userPO);

        UserPhonePO userPhonePO = new UserPhonePO();
        userPhonePO.setUserId(userId);
        userPhonePO.setPhone(DESUtils.encrypt(phone));
        userPhonePO.setStatus(CommonStatusEum.VALID_STATUS.getCode());
        userPhoneMapper.insertOne(userPhonePO);
        redisTemplate.delete(userProviderCacheKeyBuilder.buildUserPhoneObjKey(phone));//删除空值对象
        return UserLoginDTO.loginSuccess(userId,phone);
    }

    @Override
    public List<UserPhoneDTO> queryByUserId(Long userId) {
        if (userId == null ) {
            return Collections.emptyList();
        }
        String redisKey = userProviderCacheKeyBuilder.buildUserPhoneListKey(userId);
        List<Object> userPhoneList = redisTemplate.opsForList().range(redisKey, 0, -1);
        if (!CollectionUtils.isEmpty(userPhoneList)) {
            //证明是空值缓存
            if (((UserPhoneDTO) userPhoneList.get(0)).getUserId() == null) {
                return Collections.emptyList();
            }
            return userPhoneList.stream().map(x -> (UserPhoneDTO) x).collect(Collectors.toList());
        }
        List<UserPhonePO> userPhonePOS = userPhoneMapper.queryByUserId(userId);
        if (!CollectionUtils.isEmpty(userPhonePOS)) {
            List<UserPhoneDTO> userPhoneDTOS = userPhonePOS.stream().map(x -> {
                UserPhoneDTO userPhoneDTO = new UserPhoneDTO();
                userPhoneDTO.setUserId(x.getUserId());
                userPhoneDTO.setPhone(DESUtils.decrypt(x.getPhone()));
                userPhoneDTO.setStatus(x.getStatus());
                userPhoneDTO.setCreateTime(x.getCreateTime());
                userPhoneDTO.setUpdateTime(x.getUpdateTime());
                return userPhoneDTO;
            }).collect(Collectors.toList());
            //userPhoneDTOS.stream().forEach(x -> x.setPhone(DESUtils.decrypt(x.getPhone())));

            redisTemplate.opsForList().leftPushAll(redisKey, userPhoneDTOS.toArray());
            redisTemplate.expire(redisKey, 30, TimeUnit.MINUTES);
            return userPhoneDTOS;
        }
        //缓存击穿，空对象缓存
        redisTemplate.opsForList().leftPush(redisKey, new UserPhoneDTO());
        redisTemplate.expire(redisKey, 5, TimeUnit.MINUTES);
        return Collections.emptyList();
    }

    @Override
    public UserPhoneDTO queryByPhone(String phone) {
        String cacheKey = userProviderCacheKeyBuilder.buildUserPhoneObjKey(phone);
        UserPhoneDTO userPhoneDTO = (UserPhoneDTO) redisTemplate.opsForValue().get(cacheKey);
        if (userPhoneDTO != null ){
            if (userPhoneDTO.getUserId() == null ){
                //存入的为空对象，返回null
                return null;
            }
            //存入的为真实值
            return userPhoneDTO;
        }
        //缓存未命中,查询数据库
        String encryptPhone = DESUtils.encrypt(phone);
        UserPhonePO userPhonePO = userPhoneMapper.queryByPhone(encryptPhone);
        if (userPhonePO == null ){
            //缓存穿透： 存入空对象
            redisTemplate.opsForValue().set(cacheKey,new UserPhoneDTO(),5, TimeUnit.MINUTES);
            return null;
        }
        userPhoneDTO = ConvertBeanUtils.convert(userPhonePO, UserPhoneDTO.class);
        redisTemplate.opsForValue().set(cacheKey,userPhoneDTO,30,TimeUnit.MINUTES);
        return userPhoneDTO;
    }
}
