package com.quickpaas.shop.system.controller;


import com.quickpaas.framework.domain.Page;
import com.quickpaas.framework.annotation.RequirePermission;
import com.quickpaas.framework.domain.Result;
import com.quickpaas.framework.exception.WebErrorCode;
import com.quickpaas.framework.exception.WebException;
import com.quickpaas.shop.controller.BaseShopController;
import com.quickpaas.shop.system.domain.SysMenu;
import com.quickpaas.shop.system.service.SysMenuService;
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
@RequestMapping("/v1/system/sysMenu")
@Api
@Slf4j
public class SysMenuController extends BaseShopController<SysMenu>{


    private SysMenuService sysMenuService;

    @Autowired
    public SysMenuController(SysMenuService service) {
        super(service);
        this.sysMenuService = service;
    }


    @PostMapping("query")
    @ApiOperation(value = "获取列表")
    @RequirePermission("system:sysMenu:read")
    public Mono<Result<List<SysMenu>>> querySysMenuList(@RequestBody(required = false) String body) {
        Page<SysMenu> page = findList(body);
        return Mono.just(Result.list(page));
    }

    @PostMapping("get/{id}")
    @ApiOperation(value = "获取单条数据")
    @RequirePermission("system:sysMenu:read")
    public Mono<Result<SysMenu>> getSysMenuById(@PathVariable("id") Long id, @Valid SysMenu dto) {
        SysMenu newDto = getEntity(id, dto);
        if (newDto == null) {
            throw new WebException(WebErrorCode.ERROR_404);
        }
        return Mono.just(Result.ok(newDto));
    }


    @PostMapping(value = "create")
    @ApiOperation(value = "创建")
    @RequirePermission("system:sysMenu:edit")
    public Mono<Result<SysMenu>> createSysMenu(@Valid @RequestBody SysMenu dto) {
        SysMenu newDto = createEntity(dto);
        return Mono.just(Result.ok(newDto));
    }

    @PostMapping(value = "save")
    @ApiOperation(value = "创建")
    @RequirePermission("system:sysMenu:edit")
    public Mono<Result<SysMenu>> createOrSaveSysMenu(@Valid @RequestBody SysMenu dto) {
        SysMenu newDto = save(dto);
        return Mono.just(Result.ok(newDto));
    }

    @PutMapping("update/{id}")
    @ApiOperation(value = "更新")
    @RequirePermission("system:sysMenu:edit")
    public Mono<Result<SysMenu>> updateSysMenuById(@PathVariable("id") Long id, @RequestBody SysMenu dto) {
        SysMenu newDto = updateEntity(id, dto);
        return Mono.just(Result.ok(newDto));
    }


    @PostMapping("delete/{id}")
    @ApiOperation(value = "删除")
    @RequirePermission("system:sysMenu:delete")
    public Mono<Result<Boolean>> deleteSysMenuById(@PathVariable("id") Long id, @RequestBody(required = false) SysMenu dto) {
        Boolean res = deleteEntity(id, dto);
        return Mono.just(Result.ok(res));
    }


}
