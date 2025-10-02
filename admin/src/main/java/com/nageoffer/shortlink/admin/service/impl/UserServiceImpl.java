package com.nageoffer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
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
import com.nageoffer.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.nageoffer.shortlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilte;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

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
    public void Register(UserRegisterReqDTO reqDTO) {
        // 判断用户名是否存在
        if(!hasUsername(reqDTO.getUsername())){
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY+reqDTO.getUsername());
        try{
            if(lock.tryLock()){
                // 插入用户
                int result = baseMapper.insert(BeanUtil.toBean(reqDTO, UserDO.class));
                // 插入失败
                if(result <= 0){
                    throw new ClientException(UserErrorCodeEnum.USER_SAVE_ERROR);
                }
                userRegisterCachePenetrationBloomFilte.add(reqDTO.getUsername());
                return;
            }
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        }finally {
            lock.unlock();
        }
    }
    /**
     * 更新用户信息
     * @param reqDTO
     */

    @Override
    public void update(UserUpdateReqDTO reqDTO) {

        //TODO 验证当前用户名是否为登录用户

        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, reqDTO.getUsername());
        baseMapper.update(BeanUtil.toBean(reqDTO,UserDO.class), updateWrapper);

    }
    /**
     * 用户登录
     * @param reqDTO
     * @return
     */
    @Override
    public UserLoginRespDTO Login(UserLoginReqDTO reqDTO) {

        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, reqDTO.getUsername())
                .eq(UserDO::getPassword, reqDTO.getPassword())
                .eq(UserDO::getDelFlag,0 );
        UserDO userDO = (UserDO) baseMapper.selectOne(queryWrapper);
        if(userDO==  null){
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }
        Boolean hasLogin = stringRedisTemplate.hasKey( "login_"+reqDTO.getUsername());
        if(hasLogin){
            throw new ClientException("用户已登录");
        }

        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put("login_"+reqDTO.getUsername(),uuid, JSON.toJSONString(userDO));
        stringRedisTemplate.expire("login_"+reqDTO.getUsername(),30L, TimeUnit.MINUTES);

        return new UserLoginRespDTO(uuid);
    }
    /**
     * 验证用户登录
     * @param token
     * @return
     */
    @Override
    public Boolean checkLogin(String username , String token) {

        return stringRedisTemplate.opsForHash().get("login_"+username, token)!=null;
    }
    /**
     * 用户登出
     * @param token
     */
    @Override
    public void logout(String username, String token) {
        if(checkLogin(username,token)){
            stringRedisTemplate.opsForHash().delete("login_"+username, token);
            return  ;
        }
        throw new ClientException("用户TOKNE不存在或用户未登录");
    }
}
