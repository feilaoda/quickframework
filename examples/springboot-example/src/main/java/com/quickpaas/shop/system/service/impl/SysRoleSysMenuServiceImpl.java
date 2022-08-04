package com.quickpaas.shop.system.service.impl;

import com.quickpaas.framework.service.impl.BaseServiceImpl;
import com.quickpaas.shop.system.domain.SysRoleSysMenu;
import com.quickpaas.shop.system.mapper.SysRoleSysMenuMapper;
import com.quickpaas.shop.system.service.SysRoleSysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @author feilaoda
 * @email 
 * 
 */
@Service
public class SysRoleSysMenuServiceImpl extends BaseServiceImpl<SysRoleSysMenuMapper, SysRoleSysMenu> implements SysRoleSysMenuService {

    @Autowired
    private SysRoleSysMenuMapper sysRoleSysMenuMapper;

}