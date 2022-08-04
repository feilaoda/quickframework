package com.quickpaas.shop.system.controller;


import com.quickpaas.framework.domain.Page;
import com.quickpaas.framework.annotation.RequirePermission;
import com.quickpaas.framework.domain.Result;
import com.quickpaas.framework.exception.WebErrorCode;
import com.quickpaas.framework.exception.WebException;
import com.quickpaas.shop.controller.BaseShopController;
import com.quickpaas.shop.system.domain.SysRoleSysPermission;
import com.quickpaas.shop.system.service.SysRoleSysPermissionService;
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
@RequestMapping("/v1/system/sysRoleSysPermission")
@Api
@Slf4j
public class SysRoleSysPermissionController extends BaseShopController<SysRoleSysPermission>{


    private SysRoleSysPermissionService sysRoleSysPermissionService;

    @Autowired
    public SysRoleSysPermissionController(SysRoleSysPermissionService service) {
        super(service);
        this.sysRoleSysPermissionService = service;
    }


    @PostMapping("query")
    @ApiOperation(value = "获取列表")
    @RequirePermission("system:sysRoleSysPermission:read")
    public Mono<Result<List<SysRoleSysPermission>>> querySysRoleSysPermissionList(@RequestBody(required = false) String body) {
        Page<SysRoleSysPermission> page = findList(body);
        return Mono.just(Result.list(page));
    }

    @PostMapping("get/{id}")
    @ApiOperation(value = "获取单条数据")
    @RequirePermission("system:sysRoleSysPermission:read")
    public Mono<Result<SysRoleSysPermission>> getSysRoleSysPermissionById(@PathVariable("id") Long id, @Valid SysRoleSysPermission dto) {
        SysRoleSysPermission newDto = getEntity(id, dto);
        if (newDto == null) {
            throw new WebException(WebErrorCode.ERROR_404);
        }
        return Mono.just(Result.ok(newDto));
    }


    @PostMapping(value = "create")
    @ApiOperation(value = "创建")
    @RequirePermission("system:sysRoleSysPermission:edit")
    public Mono<Result<SysRoleSysPermission>> createSysRoleSysPermission(@Valid @RequestBody SysRoleSysPermission dto) {
        SysRoleSysPermission newDto = createEntity(dto);
        return Mono.just(Result.ok(newDto));
    }

    @PostMapping(value = "save")
    @ApiOperation(value = "创建")
    @RequirePermission("system:sysRoleSysPermission:edit")
    public Mono<Result<SysRoleSysPermission>> createOrSaveSysRoleSysPermission(@Valid @RequestBody SysRoleSysPermission dto) {
        SysRoleSysPermission newDto = save(dto);
        return Mono.just(Result.ok(newDto));
    }

    @PutMapping("update/{id}")
    @ApiOperation(value = "更新")
    @RequirePermission("system:sysRoleSysPermission:edit")
    public Mono<Result<SysRoleSysPermission>> updateSysRoleSysPermissionById(@PathVariable("id") Long id, @RequestBody SysRoleSysPermission dto) {
        SysRoleSysPermission newDto = updateEntity(id, dto);
        return Mono.just(Result.ok(newDto));
    }


    @PostMapping("delete/{id}")
    @ApiOperation(value = "删除")
    @RequirePermission("system:sysRoleSysPermission:delete")
    public Mono<Result<Boolean>> deleteSysRoleSysPermissionById(@PathVariable("id") Long id, @RequestBody(required = false) SysRoleSysPermission dto) {
        Boolean res = deleteEntity(id, dto);
        return Mono.just(Result.ok(res));
    }


}
