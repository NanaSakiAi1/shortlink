package com.nageoffer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.admin.dao.entity.UserDO;
import com.nageoffer.shortlink.admin.dto.req.UserLoginReqDTO;
import com.nageoffer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserRespDTO;

/**
 * 用户服务接口层
 */
public interface UserService extends IService<UserDO> {

    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     */
    UserRespDTO getUserByUsername(String username);
    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    Boolean hasUsername(String username);
    /**
     * 注册用户
     * @param reqDTO
     */
    void Register(UserRegisterReqDTO reqDTO);

    void update(UserUpdateReqDTO reqDTO);

    UserLoginRespDTO Login(UserLoginReqDTO reqDTO);

    Boolean checkLogin(String username,String token);
}
