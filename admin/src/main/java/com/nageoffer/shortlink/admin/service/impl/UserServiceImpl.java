package com.nageoffer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.admin.common.convention.exception.ClientException;
import com.nageoffer.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.nageoffer.shortlink.admin.dao.entity.UserDO;
import com.nageoffer.shortlink.admin.dao.mapper.UserMapper;
import com.nageoffer.shortlink.admin.dto.req.UserLoginReqDTO;
import com.nageoffer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserRespDTO;
import com.nageoffer.shortlink.admin.service.GroupService;
import com.nageoffer.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.nageoffer.shortlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.nageoffer.shortlink.admin.common.constant.RedisCacheConstant.USER_LOGIN_KEY;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilte;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final GroupService groupService;
    @Override
    public UserRespDTO getUserByUsername(String username) {

        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = (UserDO) baseMapper.selectOne(queryWrapper);
        if(userDO == null){
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }
        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }

    @Override
    public Boolean hasUsername(String username) {
       return !userRegisterCachePenetrationBloomFilte.contains(username);
    }

    @Override
    public void Register(UserRegisterReqDTO ReqDTO) {
        // 判断用户名是否存在
        if(!hasUsername(ReqDTO.getUsername())){
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY+ReqDTO.getUsername());
        try{
            if(lock.tryLock()){

                // 插入用户
                try {
                    int result = baseMapper.insert(BeanUtil.toBean(ReqDTO, UserDO.class));
                    if(result <= 0){
                        throw new ClientException(UserErrorCodeEnum.USER_SAVE_ERROR);
                    }
                }catch (DuplicateKeyException  ex){
                    throw new ClientException(UserErrorCodeEnum.USER_SAVE_ERROR);
                }
                // 插入失败

                userRegisterCachePenetrationBloomFilte.add(ReqDTO.getUsername());
                groupService.saveGroup(ReqDTO.getUsername(),"默认分组");
                return;
            }
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        }finally {
            lock.unlock();
        }

    }
    /**
     * 更新用户信息
     * @param ReqDTO
     */

    @Override
    public void update(UserUpdateReqDTO ReqDTO) {

        //TODO 验证当前用户名是否为登录用户

        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, ReqDTO.getUsername());
        baseMapper.update(BeanUtil.toBean(ReqDTO,UserDO.class), updateWrapper);

    }
    /**
     * 用户登录
     * @param requestParam
     * @return
     */
    @Override
    public UserLoginRespDTO Login(UserLoginReqDTO requestParam) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getPassword, requestParam.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException("用户不存在");
        }
        Map<Object, Object> hasLoginMap = stringRedisTemplate.opsForHash().entries(USER_LOGIN_KEY + requestParam.getUsername());
        if (CollUtil.isNotEmpty(hasLoginMap)) {
            stringRedisTemplate.expire(USER_LOGIN_KEY + requestParam.getUsername(), 30L, TimeUnit.MINUTES);
            String token = hasLoginMap.keySet().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElseThrow(() -> new ClientException("用户登录错误"));
            return new UserLoginRespDTO(token);
        }
        /**
         * Hash
         * Key：login_用户名
         * Value：
         *  Key：token标识
         *  Val：JSON 字符串（用户信息）
         */
        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put(USER_LOGIN_KEY + requestParam.getUsername(), uuid, JSON.toJSONString(userDO));
        stringRedisTemplate.expire(USER_LOGIN_KEY + requestParam.getUsername(), 30L, TimeUnit.MINUTES);
        return new UserLoginRespDTO(uuid);
    }
    /**
     * 验证用户登录
     * @param token
     * @return
     */
    @Override
    public Boolean checkLogin(String username , String token) {

        return stringRedisTemplate.opsForHash().get(USER_LOGIN_KEY+username, token)!=null;
    }
    /**
     * 用户登出
     * @param token
     */
    @Override
    public void logout(String username, String token) {
        if(checkLogin(username,token)){
            stringRedisTemplate.opsForHash().delete(USER_LOGIN_KEY+username, token);
            return  ;
        }
        throw new ClientException("用户TOKNE不存在或用户未登录");
    }
}
