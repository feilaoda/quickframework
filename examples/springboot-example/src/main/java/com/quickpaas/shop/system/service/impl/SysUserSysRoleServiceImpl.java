package com.quickpaas.shop.system.service.impl;

import com.quickpaas.framework.service.impl.BaseServiceImpl;
import com.quickpaas.shop.system.domain.SysUserSysRole;
import com.quickpaas.shop.system.mapper.SysUserSysRoleMapper;
import com.quickpaas.shop.system.service.SysUserSysRoleService;
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
public class SysUserSysRoleServiceImpl extends BaseServiceImpl<SysUserSysRoleMapper, SysUserSysRole> implements SysUserSysRoleService {

    @Autowired
    private SysUserSysRoleMapper sysUserSysRoleMapper;

}