package com.quickpaas.shop.system.service.impl;

import com.quickpaas.framework.service.impl.BaseServiceImpl;
import com.quickpaas.shop.system.domain.SysDepartment;
import com.quickpaas.shop.system.mapper.SysDepartmentMapper;
import com.quickpaas.shop.system.service.SysDepartmentService;
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
public class SysDepartmentServiceImpl extends BaseServiceImpl<SysDepartmentMapper, SysDepartment> implements SysDepartmentService {

    @Autowired
    private SysDepartmentMapper sysDepartmentMapper;

}