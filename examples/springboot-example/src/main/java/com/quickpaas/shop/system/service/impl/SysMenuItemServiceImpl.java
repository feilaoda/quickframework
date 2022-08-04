package com.quickpaas.shop.system.service.impl;

import com.quickpaas.framework.service.impl.BaseServiceImpl;
import com.quickpaas.shop.system.domain.SysMenuItem;
import com.quickpaas.shop.system.mapper.SysMenuItemMapper;
import com.quickpaas.shop.system.service.SysMenuItemService;
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
public class SysMenuItemServiceImpl extends BaseServiceImpl<SysMenuItemMapper, SysMenuItem> implements SysMenuItemService {

    @Autowired
    private SysMenuItemMapper sysMenuItemMapper;

}