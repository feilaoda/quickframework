package com.quickpaas.shop.system.service.impl;

import com.quickpaas.framework.service.impl.BaseServiceImpl;
import com.quickpaas.shop.system.domain.SysRoleSysPermission;
import com.quickpaas.shop.system.mapper.SysRoleSysPermissionMapper;
import com.quickpaas.shop.system.service.SysRoleSysPermissionService;
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
public class SysRoleSysPermissionServiceImpl extends BaseServiceImpl<SysRoleSysPermissionMapper, SysRoleSysPermission> implements SysRoleSysPermissionService {

    @Autowired
    private SysRoleSysPermissionMapper sysRoleSysPermissionMapper;

}