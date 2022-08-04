package com.quickpaas.shop.system.controller;


import com.quickpaas.framework.domain.Page;
import com.quickpaas.framework.annotation.RequirePermission;
import com.quickpaas.framework.domain.Result;
import com.quickpaas.framework.exception.WebErrorCode;
import com.quickpaas.framework.exception.WebException;
import com.quickpaas.shop.controller.BaseShopController;
import com.quickpaas.shop.system.domain.SysRoleSysMenu;
import com.quickpaas.shop.system.service.SysRoleSysMenuService;
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
@RequestMapping("/v1/system/sysRoleSysMenu")
@Api
@Slf4j
public class SysRoleSysMenuController extends BaseShopController<SysRoleSysMenu>{


    private SysRoleSysMenuService sysRoleSysMenuService;

    @Autowired
    public SysRoleSysMenuController(SysRoleSysMenuService service) {
        super(service);
        this.sysRoleSysMenuService = service;
    }


    @PostMapping("query")
    @ApiOperation(value = "获取列表")
    @RequirePermission("system:sysRoleSysMenu:read")
    public Mono<Result<List<SysRoleSysMenu>>> querySysRoleSysMenuList(@RequestBody(required = false) String body) {
        Page<SysRoleSysMenu> page = findList(body);
        return Mono.just(Result.list(page));
    }

    @PostMapping("get/{id}")
    @ApiOperation(value = "获取单条数据")
    @RequirePermission("system:sysRoleSysMenu:read")
    public Mono<Result<SysRoleSysMenu>> getSysRoleSysMenuById(@PathVariable("id") Long id, @Valid SysRoleSysMenu dto) {
        SysRoleSysMenu newDto = getEntity(id, dto);
        if (newDto == null) {
            throw new WebException(WebErrorCode.ERROR_404);
        }
        return Mono.just(Result.ok(newDto));
    }


    @PostMapping(value = "create")
    @ApiOperation(value = "创建")
    @RequirePermission("system:sysRoleSysMenu:edit")
    public Mono<Result<SysRoleSysMenu>> createSysRoleSysMenu(@Valid @RequestBody SysRoleSysMenu dto) {
        SysRoleSysMenu newDto = createEntity(dto);
        return Mono.just(Result.ok(newDto));
    }

    @PostMapping(value = "save")
    @ApiOperation(value = "创建")
    @RequirePermission("system:sysRoleSysMenu:edit")
    public Mono<Result<SysRoleSysMenu>> createOrSaveSysRoleSysMenu(@Valid @RequestBody SysRoleSysMenu dto) {
        SysRoleSysMenu newDto = save(dto);
        return Mono.just(Result.ok(newDto));
    }

    @PutMapping("update/{id}")
    @ApiOperation(value = "更新")
    @RequirePermission("system:sysRoleSysMenu:edit")
    public Mono<Result<SysRoleSysMenu>> updateSysRoleSysMenuById(@PathVariable("id") Long id, @RequestBody SysRoleSysMenu dto) {
        SysRoleSysMenu newDto = updateEntity(id, dto);
        return Mono.just(Result.ok(newDto));
    }


    @PostMapping("delete/{id}")
    @ApiOperation(value = "删除")
    @RequirePermission("system:sysRoleSysMenu:delete")
    public Mono<Result<Boolean>> deleteSysRoleSysMenuById(@PathVariable("id") Long id, @RequestBody(required = false) SysRoleSysMenu dto) {
        Boolean res = deleteEntity(id, dto);
        return Mono.just(Result.ok(res));
    }


}
