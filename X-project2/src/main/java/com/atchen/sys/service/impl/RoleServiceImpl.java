package com.atchen.sys.service.impl;

import com.atchen.sys.entity.Role;
import com.atchen.sys.entity.RoleMenu;
import com.atchen.sys.mapper.RoleMapper;
import com.atchen.sys.mapper.RoleMenuMapper;
import com.atchen.sys.service.IRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author atchen
 * @since 2023-09-08
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
    @Resource
    private RoleMenuMapper roleMenuMapper;
    @Override
    @Transactional
    public void addRole(Role role) {
//        写入角色表
            this.baseMapper.insert(role);
//        写入角色和菜单的对照表
            if(null != role.getMenuIdList()){
                for (Integer menuId : role.getMenuIdList()){
                    roleMenuMapper.insert(new RoleMenu(null,role.getRoleId(),menuId));
                }
            }
    }

    @Override
    public Role getRoleById(Integer id) {
//       获取用户id的角色对象
        Role role = this.baseMapper.selectById(id);
//       但是我们只想要二级的列表,于是需要实现关联查询,找到菜单表里二级功能叶子节点
        List<Integer> menuIdList = roleMenuMapper.getMenuIdListByRoleId(id);
//        连接权限和角色
        role.setMenuIdList(menuIdList);
        return role;
    }

    @Override
    @Transactional
    public void updateRole(Role role) {
//        进来的role是要改成的角色
//
        this.baseMapper.updateById(role);
//        删除原有RoleMenu中的相关权限，因为是老的数据
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId,role.getRoleId());
        roleMenuMapper.delete(wrapper);
//        新增相关权限，是用户想要给某角色的权限
        if(null != role.getMenuIdList()){
            for (Integer menuId : role.getMenuIdList()){
                roleMenuMapper.insert(new RoleMenu(null,role.getRoleId(),menuId));
            }
        }
    }

    @Override
    public void deleteRoleById(Integer id) {
//        删掉角色表的实体对象
        this.baseMapper.deleteById(id);
//        删除原有RoleMenu中的相关权限，因为是老的数据
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId,id);
        roleMenuMapper.delete(wrapper);
    }

}
