package com.atchen.sys.service.impl;

import com.atchen.common.utils.JwtUtil;
import com.atchen.sys.entity.Menu;
import com.atchen.sys.entity.User;
import com.atchen.sys.entity.UserRole;
import com.atchen.sys.mapper.UserMapper;
import com.atchen.sys.mapper.UserRoleMapper;
import com.atchen.sys.service.IMenuService;
import com.atchen.sys.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author atchen
 * @since 2023-09-08
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
//    在接口实现类中，不仅继承了系统提供的方法，还实现了service接口。

//    Map<String,Object> login(User user);
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Autowired
    private IMenuService menuService;
    @Override
    public Map<String, Object> login(User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
//        判断用户名是否相等
        wrapper.eq(User::getUsername,user.getUsername());
//        登录用户挑选一个。
        User loginUser = this.baseMapper.selectOne(wrapper);
//        如果发现用户名有注册过，且密码匹配。
        if(loginUser != null && passwordEncoder.matches(user.getPassword(),loginUser.getPassword() )){
//        生成token
//            String key = "user:" + UUID.randomUUID();
//          配置Redis，存入
            loginUser.setPassword(null);
//            创建jwt
            String token = jwtUtil.createToken(loginUser);
//            opsForValue是redis的方法，会对值进行操作，这里是新增一个键值对。
//            redisTemplate.opsForValue().set(key,loginUser,30, TimeUnit.MINUTES);
//            创建返回用的哈希map，然后把token存进去，返回data给登录方法
            HashMap<String, Object> data = new HashMap<>();
            data.put("token",token);
            return data;
        }

        return null;
    }

    @Override
    public Map<String, Object> getUserInfo(String token) {
//        功能是：对前端发来的token在数据库中查找用户
//       1. 对redis操作，获取了存的对象，类型obj
//        Object obj = redisTemplate.opsForValue().get(token);
        User loginuser = null;
        try {
             loginuser = jwtUtil.parseToken(token, User.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(loginuser != null){
//         2.   取到了obj,转为json字符串,才能用parseObject对字符串进行一一序列化为User的对象。
//           User loginUser = JSON.parseObject(JSON.toJSONString(obj),User.class);
//          3对获得的数据进行存储，返回，考虑使用哈希表进行数据存储，返回，容易序列化为json
           HashMap<String, Object> data = new HashMap<>();
           data.put("name",loginuser.getUsername());
           data.put("avatar", loginuser.getAvatar());
//           在json中角色是个数组，人可能身兼数职
//            角色在另一个表中，并用一张表关联角色和用户
//            这里要写一个关联查询
            List<String> rolelist = this.baseMapper.getRoleNameByUserId(loginuser.getId());
            data.put("roles",rolelist);

//            权限列表关联查询
            List<Menu> menuList = menuService.getMenuListByUserId(loginuser.getId());
            data.put("menuList",menuList);

            return data;
        }

        return null;
    }
//    登录后，利用token进行注销


    @Override
    public void deleteUser(String token) {
//         Object obj = redisTemplate.opsForValue().get(token);
//        if(obj!=null){
//            redisTemplate.delete(token);
//            return 1;
//        }
//        return 0;
    }

    @Override
    public User getUserById(Integer id) {
       User user = this.baseMapper.selectById(id);
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId,id);
        List<UserRole> userRoles = userRoleMapper.selectList(wrapper);

        List<Integer> roleIdList = userRoles.stream()
                .map(userRole -> {return userRole.getRoleId();})
                .collect(Collectors.toList());
        user.setRoleIdList(roleIdList);
        return user;
    }

    @Override
    @Transactional
    public void addUser(User user) {
//        写入用户表
        this.baseMapper.insert(user);
//        写入角色表
        List<Integer> list = user.getRoleIdList();
        if (list != null) {
            for (Integer roleId : list) {
                userRoleMapper.insert(new UserRole(null, user.getId(), roleId));
            }
        }
    }

    @Override
    @Transactional
    public void updateUser(User user) {
//        更新用户表本身
        this.baseMapper.updateById(user);
//        清除原有角色
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId,user.getId());
        userRoleMapper.delete(wrapper);
//        设置新的角色
        List<Integer> list = user.getRoleIdList();
        if (list != null) {
            for (Integer roleId : list) {
                userRoleMapper.insert(new UserRole(null, user.getId(), roleId));
            }
        }
    }

    @Override
    public void deleteUserById(Integer id) {
        this.baseMapper.deleteById(id);

        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId,id);
        userRoleMapper.delete(wrapper);


    }


}
