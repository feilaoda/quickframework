package com.quickpaas.shop.system.controller;


import com.quickpaas.framework.domain.Page;
import com.quickpaas.framework.annotation.RequirePermission;
import com.quickpaas.framework.domain.Result;
import com.quickpaas.framework.exception.WebErrorCode;
import com.quickpaas.framework.exception.WebException;
import com.quickpaas.shop.controller.BaseShopController;
import com.quickpaas.shop.system.domain.SysUserSysRole;
import com.quickpaas.shop.system.service.SysUserSysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

/**
 * Description:
 *
 * @author feilaoda
 * @email 
 * @date 2022
 */
@RestController
@RequestMapping("/v1/system/sysUserSysRole")
@Api
@Slf4j
public class SysUserSysRoleController extends BaseShopController<SysUserSysRole>{


    private SysUserSysRoleService sysUserSysRoleService;

    @Autowired
    public SysUserSysRoleController(SysUserSysRoleService service) {
        super(service);
        this.sysUserSysRoleService = service;
    }


    @PostMapping("query")
    @ApiOperation(value = "获取列表")
    @RequirePermission("system:sysUserSysRole:read")
    public Mono<Result<List<SysUserSysRole>>> querySysUserSysRoleList(@RequestBody(required = false) String body) {
        Page<SysUserSysRole> page = findList(body);
        return Mono.just(Result.list(page));
    }

    @PostMapping("get/{id}")
    @ApiOperation(value = "获取单条数据")
    @RequirePermission("system:sysUserSysRole:read")
    public Mono<Result<SysUserSysRole>> getSysUserSysRoleById(@PathVariable("id") Long id, @Valid SysUserSysRole dto) {
        SysUserSysRole newDto = getEntity(id, dto);
        if (newDto == null) {
            throw new WebException(WebErrorCode.ERROR_404);
        }
        return Mono.just(Result.ok(newDto));
    }


    @PostMapping(value = "create")
    @ApiOperation(value = "创建")
    @RequirePermission("system:sysUserSysRole:edit")
    public Mono<Result<SysUserSysRole>> createSysUserSysRole(@Valid @RequestBody SysUserSysRole dto) {
        SysUserSysRole newDto = createEntity(dto);
        return Mono.just(Result.ok(newDto));
    }

    @PostMapping(value = "save")
    @ApiOperation(value = "创建")
    @RequirePermission("system:sysUserSysRole:edit")
    public Mono<Result<SysUserSysRole>> createOrSaveSysUserSysRole(@Valid @RequestBody SysUserSysRole dto) {
        SysUserSysRole newDto = save(dto);
        return Mono.just(Result.ok(newDto));
    }

    @PutMapping("update/{id}")
    @ApiOperation(value = "更新")
    @RequirePermission("system:sysUserSysRole:edit")
    public Mono<Result<SysUserSysRole>> updateSysUserSysRoleById(@PathVariable("id") Long id, @RequestBody SysUserSysRole dto) {
        SysUserSysRole newDto = updateEntity(id, dto);
        return Mono.just(Result.ok(newDto));
    }


    @PostMapping("delete/{id}")
    @ApiOperation(value = "删除")
    @RequirePermission("system:sysUserSysRole:delete")
    public Mono<Result<Boolean>> deleteSysUserSysRoleById(@PathVariable("id") Long id, @RequestBody(required = false) SysUserSysRole dto) {
        Boolean res = deleteEntity(id, dto);
        return Mono.just(Result.ok(res));
    }


}
