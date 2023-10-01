package com.atchen.sys.service;

import com.atchen.sys.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author atchen
 * @since 2023-09-08
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> getAllMenu();
    List<Menu> getMenuListByUserId(Integer userId);
}
