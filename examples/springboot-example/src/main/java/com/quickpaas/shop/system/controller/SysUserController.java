package com.quickpaas.shop.system.controller;


import com.quickpaas.framework.domain.Page;
import com.quickpaas.framework.annotation.RequirePermission;
import com.quickpaas.framework.domain.Result;
import com.quickpaas.framework.exception.WebException;
import com.quickpaas.framework.quickql.QueryRequest;
import com.quickpaas.shop.controller.BaseShopController;
import com.quickpaas.shop.controller.BaseShopController;
import com.quickpaas.shop.system.domain.SysUser;
import com.quickpaas.shop.system.service.SysDepartmentService;
import com.quickpaas.shop.system.service.SysUserService;
import com.quickpaas.shop.system.service.SysUserSysRoleService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

import static com.quickpaas.framework.exception.WebErrorCode.ERROR_404;

/**
 * Description:
 *
 * @author feilaoda
 * @email
 *
 */
@RestController
@RequestMapping("/v1/system/sysUser")
@Slf4j
public class SysUserController extends BaseShopController<SysUser> {


    @Autowired
    private final SysUserService sysUserService;

    @Autowired
    private SysUserSysRoleService sysUserSysRoleService;

    @Autowired
    private SysDepartmentService sysDepartmentService;


    @Autowired
    public SysUserController(SysUserService sysUserService) {
        super(sysUserService);
        this.sysUserService = sysUserService;
    }

    //important
    public void beforeQuery(QueryRequest request) {
        request.getOrderBy().add("-id");
    }

    @Override
    public SysUser updateEntity(Serializable id, SysUser dto) {
        if (StringUtils.isEmpty(dto.getPassword())) {
            dto.setPassword(null);
        }
        dto.setAccount(null);
        SysUser newEntity = super.updateEntity(id, dto);
        return newEntity;
    }


    @PostMapping("query")
    @ApiOperation(value = "获取列表")
    @RequirePermission("system:sysUser:read")
    public Mono<Result<List<SysUser>>> queryUserList(@RequestBody(required = false) String body) {
        Page<SysUser> page = findList(body);
        return Mono.just(Result.list(page));
    }

    @PostMapping("get/{id}")
    @ApiOperation(value = "获取单条数据")
    @RequirePermission("system:sysUser:read")
    public Mono<Result<SysUser>> getUserById(@PathVariable("id") Long id, @RequestBody(required = false) String body) {
        SysUser newDto = getEntity(id, body);
        if (newDto == null) {
            throw new WebException(ERROR_404);
        }
        return Mono.just(Result.ok(newDto));
    }


    @PostMapping(value = "save")
    @ApiOperation(value = "创建")
    @RequirePermission("system:sysUser:edit")
    public Mono<Result<SysUser>> createOrSaveUser(@Valid @RequestBody SysUser dto) {
        checkUnique( "账号", "account", dto.getAccount());
        SysUser newDto = save(dto);
        return Mono.just(Result.ok(newDto));
    }

    @PostMapping(value = "create")
    @ApiOperation(value = "创建")
    @RequirePermission("system:sysUser:edit")
    public Mono<Result<SysUser>> createUser(@Valid @RequestBody SysUser dto) {
        SysUser newDto = createEntity(dto);
        return Mono.just(Result.ok(newDto));
    }

    @PutMapping("update/{id}")
    @ApiOperation(value = "更新")
    @RequirePermission("system:sysUser:edit")
    public Mono<Result<SysUser>> updateUserById(@PathVariable("id") Long id, @RequestBody SysUser dto) {
        SysUser newDto = updateEntity(id, dto);
        return Mono.just(Result.ok(newDto));
    }


    @PostMapping("delete/{id}")
    @ApiOperation(value = "删除")
    @RequirePermission("system:sysUser:delete")
    public Mono<Result<Boolean>> deleteUserById(@PathVariable("id") Long id, @RequestBody(required = false) SysUser dto) {
        Boolean res = deleteEntity(id, dto);
        return Mono.just(Result.ok(res));
    }

}
