package com.quickpaas.shop.system.service.impl;

import com.quickpaas.framework.service.impl.BaseServiceImpl;
import com.quickpaas.shop.system.domain.SysUser;
import com.quickpaas.shop.system.entity.SysUserEntity;
import com.quickpaas.shop.system.mapper.SysUserMapper;
import com.quickpaas.shop.system.service.SysUserService;
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
public class SysUserServiceImpl extends BaseServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

}