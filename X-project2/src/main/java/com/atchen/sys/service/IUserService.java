package com.atchen.sys.service;

import com.atchen.sys.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author atchen
 * @since 2023-09-08
 */
public interface IUserService extends IService<User> {
    Map<String,Object> login(User user);
    Map<String,Object> getUserInfo(String token);

    void deleteUser(String token);

    User getUserById(Integer id);

    void addUser(User user);

    void updateUser(User user);

    void deleteUserById(Integer id);
}
