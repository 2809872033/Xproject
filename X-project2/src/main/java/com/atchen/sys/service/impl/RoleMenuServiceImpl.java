package com.atchen.sys.service.impl;

import com.atchen.sys.entity.RoleMenu;
import com.atchen.sys.mapper.RoleMenuMapper;
import com.atchen.sys.service.IRoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.models.auth.In;
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
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements IRoleMenuService {
}
