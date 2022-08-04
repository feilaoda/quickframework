package com.quickpaas.shop.system.controller;


import com.quickpaas.framework.domain.Page;
import com.quickpaas.framework.annotation.RequirePermission;
import com.quickpaas.framework.domain.Result;
import com.quickpaas.framework.exception.WebException;
import com.quickpaas.shop.controller.BaseShopController;
import com.quickpaas.shop.system.domain.SysRole;
import com.quickpaas.shop.system.service.SysRoleService;
import com.quickpaas.shop.system.service.SysRoleSysPermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
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
@RequestMapping("/v1/system/sysRole")
@Api
@Slf4j
public class SysRoleController extends BaseShopController<SysRole> {

    private final SysRoleService sysRoleService;

    @Autowired
    private SysRoleSysPermissionService sysRoleSysPermissionService;

    @Autowired
    public SysRoleController(SysRoleService sysRoleService) {
        super(sysRoleService);
        this.sysRoleService = sysRoleService;
    }

    @PostMapping("query")
    @ApiOperation(value = "获取列表")
    @RequirePermission("system:sysRole:read")
    public Mono<Result<List<SysRole>>> queryRoleList(@RequestBody(required = false) String body) {
        Page<SysRole> page = findList(body);
        return Mono.just(Result.list(page));
    }

    @PostMapping("get/{id}")
    @ApiOperation(value = "获取单条数据")
    @RequirePermission("system:sysRole:read")
    public Mono<Result<SysRole>> getRoleById(@PathVariable("id") Long id, @RequestBody(required = false) String body) {
        SysRole newDto = getEntity(id, body);
        if (newDto == null) {
            throw new WebException(ERROR_404);
        }
        return Mono.just(Result.ok(newDto));
    }


    @PostMapping(value = "save")
    @ApiOperation(value = "创建")
    @RequirePermission("system:sysRole:edit")
    public Mono<Result<SysRole>> createOrSaveRole(@Valid @RequestBody SysRole dto) {
        checkUnique( "角色名称", "name", dto.getName());
        if (dto.tid() == null) {

            SysRole newDto = createEntity(dto);
            return Mono.just(Result.ok(newDto));
        } else {
            SysRole newDto = updateEntity(dto.tid(), dto);
            return Mono.just(Result.ok(newDto));
        }
    }

    @PostMapping(value = "create")
    @ApiOperation(value = "创建")
    @RequirePermission("system:sysRole:edit")
    public Mono<Result<SysRole>> createRole(@Valid @RequestBody SysRole dto) {
        SysRole newDto = createEntity(dto);
        return Mono.just(Result.ok(newDto));
    }

    @PutMapping("update/{id}")
    @ApiOperation(value = "更新")
    @RequirePermission("system:sysRole:edit")
    public Mono<Result<SysRole>> updateRoleById(@PathVariable("id") Long id, @RequestBody SysRole dto) {
        SysRole newDto = updateEntity(id, dto);
        return Mono.just(Result.ok(newDto));
    }


    @PostMapping("delete/{id}")
    @ApiOperation(value = "删除")
    @RequirePermission("system:sysRole:delete")
    public Mono<Result<Boolean>> deleteRoleById(@PathVariable("id") Long id, @RequestBody(required = false) SysRole dto) {
        Boolean res = deleteEntity(id, dto);
        return Mono.just(Result.ok(res));
    }


}
