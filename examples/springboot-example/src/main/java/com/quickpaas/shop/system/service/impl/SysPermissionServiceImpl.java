package com.quickpaas.shop.system.service.impl;

import com.quickpaas.framework.service.impl.BaseServiceImpl;
import com.quickpaas.shop.system.domain.SysPermission;
import com.quickpaas.shop.system.mapper.SysPermissionMapper;
import com.quickpaas.shop.system.service.SysPermissionService;
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
public class SysPermissionServiceImpl extends BaseServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

}