package com.atchen.sys.controller;

import com.atchen.common.vo.Result;
import com.atchen.sys.entity.User;
import com.atchen.sys.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author atchen
 * @since 2023-09-08
 */
@Api(tags = {"用户接口列表"})
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

//    查找已存在的所有用户
    @GetMapping("/all")
//    从
    public Result<List<User>> getAllUser(){
        List<User> list = userService.list();
        return Result.success(list,"success");
    }
//    首先写个用户登录
//    对原有的接口进行更改，以实现动态菜单
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<Map<String,Object>> login(@RequestBody User user){

        Map<String,Object> data = userService.login(user);
        if(data != null){
            return Result.success(data);
        }
        return Result.fail(20002,"用户名或密码错误");
    }
    // 获取用户信息接口，辅助登录,如果token未失效则可查到返回的对象
    @GetMapping("/info")
    public Result<Map<String,Object>> getUserInfo(@RequestParam("token") String token){
//
        Map<String,Object> data = userService.getUserInfo(token);
        if(data != null){
            return Result.success(data);
        }
        return Result.fail(20003,"登录信息已无效，请重新登录");
    }
// 创建新用户（增加用户记录）
    @PostMapping
    public Result<?> addUser(@RequestBody User user){
//        对获取到的实体类对象进行
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.addUser(user);
        return  Result.success("新增用户成功");
    }

//   删除用户
    @PostMapping("/logout")
    public Result<?> deleteUser(@RequestHeader("X-Token") String token){
        userService.deleteUser(token);
            return  Result.success();
    }
    //    查询单个用户的信息，用于前端修改
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable("id") Integer id){
        User user = userService.getUserById(id);
        return Result.success(user);
    }
    @PutMapping
    public Result<?> updateUser(@RequestBody User user){
        user.setPassword(null);
        userService.updateUser(user);
            return Result.success("用户信息修改成功"+user);

    }
//查询用户
    @GetMapping("/list")
    public Result<Map<String,Object>> SearchUser(
                                                 @RequestParam(value = "username",required = false) String username,
                                                 @RequestParam(value = "phone",required = false) String phone,
                                                 @RequestParam("pageNo") Long pageNo,
                                                 @RequestParam("pageSize") Long pageSize
                                                 ){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
//        通过eq方法的重载方法,保证传进来的username不为空
        wrapper.eq(StringUtils.hasLength(username),User::getUsername,username);
        wrapper.eq(StringUtils.hasLength(phone),User::getPhone,phone);
//        分页原理要明白，传入分页
//        首先是前端的page位置，传给后端，让后端计算数据分页情况和当前位置
        Page<User> page = new Page<>(pageNo,pageSize);
//        把分页情况和查询条件传给数据库
        userService.page(page,wrapper);
//        在哈希map里放获得的条目数和详细记录
        Map<String,Object> data = new HashMap<>();
        data.put("total",page.getTotal());
        data.put("rows",page.getRecords());

        return Result.success(data);
    }

// 逻辑删除用户数据
    @DeleteMapping("/{id}")
    public Result<User> deleteUserById(@PathVariable("id") Integer id){
       userService.deleteUserById(id);
        return  Result.success("删除用户成功");
    }


}
