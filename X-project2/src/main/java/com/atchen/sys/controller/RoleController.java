package com.atchen.sys.controller;

import com.atchen.common.vo.Result;
import com.atchen.sys.entity.Role;
import com.atchen.sys.entity.User;
import com.atchen.sys.service.IRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private IRoleService roleService;


    //查询用户
    @GetMapping("/list")
    public Result<Map<String,Object>> searchRoleList(
            @RequestParam(value = "rolename",required = false) String rolename,
            @RequestParam("pageNo") Long pageNo,
            @RequestParam("pageSize") Long pageSize
    ){
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
//        通过eq方法的重载方法,保证传进来的rolename不为空
        wrapper.eq(StringUtils.hasLength(rolename),Role::getRoleName,rolename);
        wrapper.orderByDesc(Role::getRoleId);
//        分页原理要明白，传入分页
//        首先是前端的page位置，传给后端，让后端计算数据分页情况和当前位置
        Page<Role> page = new Page<>(pageNo,pageSize);
//        把分页情况和查询条件传给数据库
        roleService.page(page,wrapper);
//        在哈希map里放获得的条目数和详细记录
        Map<String,Object> data = new HashMap<>();
        data.put("total",page.getTotal());
        data.put("rows",page.getRecords());

        return Result.success(data);
    }
    @PostMapping
    public Result<?> addRole(@RequestBody Role role){
        roleService.addRole(role);

        return  Result.success("新增角色成功");
    }
    @PutMapping
    public Result<?> updateRole(@RequestBody Role role){
        roleService.updateRole(role);
        return  Result.success("修改角色成功");
    }
    @GetMapping("/{id}")
    public Result<Role> getRoleById(@PathVariable("id") Integer id){
        Role role = roleService.getRoleById(id);
        return Result.success(role);
    }

    // 逻辑删除用户数据
    @DeleteMapping("/{id}")
    public Result<User> deleteRoleById(@PathVariable("id") Integer id){
        roleService.deleteRoleById(id);
        return  Result.success("删除角色成功");
    }
//    获取用户的全部角色
    @GetMapping("/all")
    public Result<List<Role>> getAllRole(){
        List<Role> roleList = roleService.list();
        return Result.success(roleList);
    }
}
