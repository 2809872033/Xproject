package com.atchen.sys.service.impl;

import com.atchen.sys.entity.Menu;
import com.atchen.sys.mapper.MenuMapper;
import com.atchen.sys.service.IMenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Override
    public List<Menu> getAllMenu() {
//        这个服务主要是调用数据库中的数据组成层级结构树
//        查页面菜单一级
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getParentId, 0);
        List<Menu> menuList = this.list(wrapper);
//       填充子菜单
        setMenuChildren(menuList);
        return menuList;
    }


    private void setMenuChildren(List<Menu> menuList){
        if(menuList !=null){
            for (Menu menu : menuList){
                LambdaQueryWrapper<Menu> subWrapper = new LambdaQueryWrapper<>();
                subWrapper.eq(Menu::getParentId,menu.getMenuId());
                List<Menu> subMenuList = this.list(subWrapper);
                menu.setChildren(subMenuList);
//                递归执行上面的内容，获取完整的子菜单
                setMenuChildren(subMenuList);
            }
        }
    }

    @Override
    public List<Menu> getMenuListByUserId(Integer userId) {
//        查一级菜单
        List<Menu> menuList = this.baseMapper.getMenuListByUserId(userId, 0);
//        查子菜单
        setMenuChildrenByUserId(userId, menuList);
        return menuList;
    }

    private void setMenuChildrenByUserId(Integer userId, List<Menu> menuList) {
        if(menuList != null){
            for (Menu menu: menuList){
                List<Menu> subMenuList = this.baseMapper.getMenuListByUserId(userId,menu.getMenuId());
                menu.setChildren(subMenuList);
//               递归
                setMenuChildrenByUserId(userId,subMenuList);
            }
        }
    }

}
