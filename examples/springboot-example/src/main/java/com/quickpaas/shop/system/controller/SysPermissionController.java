package com.quickpaas.shop.system.controller;


import com.quickpaas.framework.domain.Page;
import com.quickpaas.framework.annotation.RequirePermission;
import com.quickpaas.framework.domain.Result;
import com.quickpaas.framework.exception.WebException;
import com.quickpaas.shop.controller.BaseShopController;
import com.quickpaas.shop.system.domain.SysPermission;
import com.quickpaas.shop.system.service.SysPermissionService;
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
@RequestMapping("/v1/system/sysPermission")
@Api
@Slf4j
public class SysPermissionController extends BaseShopController<SysPermission> {


    private final SysPermissionService sysPermissionService;

    @Autowired
    public SysPermissionController(SysPermissionService sysPermissionService) {
        super(sysPermissionService);
        this.sysPermissionService = sysPermissionService;
    }

    @PostMapping("query")
    @ApiOperation(value = "获取列表")
    @RequirePermission("system:sysPermission:read")
    public Mono<Result<List<SysPermission>>> queryPermissionList(@RequestBody(required = false) String body) {
        Page<SysPermission> page = findList(body);
        return Mono.just(Result.list(page));
    }

    @PostMapping("get/{id}")
    @ApiOperation(value = "获取单条数据")
    @RequirePermission("system:sysPermission:read")
    public Mono<Result<SysPermission>> getPermissionById(@PathVariable("id") Long id, @Valid SysPermission dto) {
        SysPermission newDto = getEntity(id, dto);
        if (newDto == null) {
            throw new WebException(ERROR_404);
        }
        return Mono.just(Result.ok(newDto));
    }


    @PostMapping(value = "save")
    @ApiOperation(value = "创建")
    @RequirePermission("system:sysPermission:edit")
    public Mono<Result<SysPermission>> createOrSavePermission(@Valid @RequestBody SysPermission dto) {
        checkUnique( "权限名称", "name", dto.getName());
        if (dto.tid() == null) {

            SysPermission newDto = createEntity(dto);
            return Mono.just(Result.ok(newDto));
        } else {
            SysPermission newDto = updateEntity(dto.tid(), dto);
            return Mono.just(Result.ok(newDto));
        }
    }

    @PostMapping(value = "create")
    @ApiOperation(value = "创建")
    @RequirePermission("system:sysPermission:edit")
    public Mono<Result<SysPermission>> createPermission(@Valid @RequestBody SysPermission dto) {
        SysPermission newDto = createEntity(dto);
        return Mono.just(Result.ok(newDto));
    }

    @PutMapping("update/{id}")
    @ApiOperation(value = "更新")
    @RequirePermission("system:sysPermission:edit")
    public Mono<Result<SysPermission>> updatePermissionById(@PathVariable("id") Long id, @RequestBody SysPermission dto) {
        SysPermission newDto = updateEntity(id, dto);
        return Mono.just(Result.ok(newDto));
    }


    @PostMapping("delete/{id}")
    @ApiOperation(value = "删除")
    @RequirePermission("system:sysPermission:delete")
    public Mono<Result<Boolean>> deletePermissionById(@PathVariable("id") Long id, @RequestBody(required = false) SysPermission dto) {
        Boolean res = deleteEntity(id, dto);
        return Mono.just(Result.ok(res));
    }

}
