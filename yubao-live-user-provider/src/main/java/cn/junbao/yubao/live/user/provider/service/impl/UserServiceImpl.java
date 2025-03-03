package cn.junbao.yubao.live.user.provider.service.impl;

import cn.junbao.yubao.live.common.interfaces.utils.ConvertBeanUtils;
import cn.junbao.yubao.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import cn.junbao.yubao.live.user.dto.UserDTO;
import cn.junbao.yubao.live.user.provider.dao.mapper.IUserMapper;
import cn.junbao.yubao.live.user.provider.dao.po.UserPO;
import cn.junbao.yubao.live.user.provider.service.IUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {
    @Resource
    private IUserMapper userMapper;
    @Resource
    private RedisTemplate<String,UserDTO> redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public UserDTO getUserById(Long userId) {

        if (userId == null){
            return null;
        }
        //查询缓存
        String cacheKey = cacheKeyBuilder.buildUserInfoKey(userId);
        //String cacheKey = "user-"+userId;
        UserDTO userDTO = redisTemplate.opsForValue().get(cacheKey);
        if (userDTO != null) {
            return userDTO;
        }

        //查询数据库
        UserPO user = userMapper.getUserById(userId);
        if (user != null){
            userDTO = ConvertBeanUtils.convert(user, UserDTO.class);
            //存入redis缓存
            redisTemplate.opsForValue().set(cacheKey,userDTO,30, TimeUnit.MINUTES);
        }
        return userDTO;
    }

    @Override
    public Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList) {

        log.info("batchQueryUserInfo userId的集合为:{}",userIdList);
        //1. 查询缓存
        List<String> cacheKeyList = new ArrayList<>();
        userIdList.forEach(userId -> {
            cacheKeyList.add(cacheKeyBuilder.buildUserInfoKey(userId));
        });
        // -- 由于multiGet 当查询不到时，会使用null进行填充该位置，所以需要进行非空过滤
        List<UserDTO> userDTOList = redisTemplate.opsForValue().multiGet(cacheKeyList).stream().filter(x-> x != null).collect(Collectors.toList());

        //1.1 若缓存中全部命中，则直接返回
        if (!userDTOList.isEmpty() && userDTOList.size() == userIdList.size()){
            return userDTOList.stream().collect(Collectors.toMap(UserDTO::getUserId,x->x));
        }

        //1.2 整理缓存未命中的id集合
        List<Long> userIdsInCache = userDTOList.stream().map(UserDTO::getUserId).collect(Collectors.toList());
        List<Long> userIdsNoInCache = userIdList.stream().filter(x -> !userIdsInCache.contains(x)).collect(Collectors.toList());

        //2. 查询数据库  ： 使用多线程 代替 union all
        //2.1. 将userid进行分组,满足分表要求
        Map<Long, List<Long>> userIdsMap = userIdsNoInCache.stream().collect(Collectors.groupingBy(userId -> userId % 4));
        //2.2 使用parallelStream多线程进行并行查询
        List<UserDTO> userDTOInDBList = new CopyOnWriteArrayList<>();
        userIdsMap.values().parallelStream().forEach(queryUserIdList -> {
            userDTOInDBList.addAll(ConvertBeanUtils.convertList(userMapper.getBatchByUserIds(queryUserIdList),UserDTO.class));
        });
        if (!userDTOInDBList.isEmpty()){
            //2.3 将从DB中查询的数据 加载到缓存中
            Map<String, UserDTO> userDTOInDBMap = userDTOInDBList.stream().collect(Collectors.toMap(userDTO -> cacheKeyBuilder.buildUserInfoKey(userDTO.getUserId()), x -> x));
            redisTemplate.opsForValue().multiSet(userDTOInDBMap);
            // -- executePipelined 通过管道，将命令一同发送到redis，减少网络IO
            redisTemplate.executePipelined(new SessionCallback<Object>() {
                @Override
                public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                    for (String cacheKey : userDTOInDBMap.keySet()) {
                        operations.expire((K) cacheKey,createRandomExpireTime(),TimeUnit.SECONDS);
                    }
                    return null;
                }
            });

            //添加到结果集中
            userDTOList.addAll(userDTOInDBList);
        }
        //返回数据
        return userDTOList.stream().collect(Collectors.toMap(UserDTO::getUserId,x -> x));
    }

    private int createRandomExpireTime() {
        int time = ThreadLocalRandom.current().nextInt(500);
        return time + 60*30;// 30 min + randomTime  [second]
    }
}
