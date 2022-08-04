package com.quickpaas.shop.system.controller;


import com.quickpaas.framework.domain.Page;
import com.quickpaas.framework.annotation.RequirePermission;
import com.quickpaas.framework.domain.Result;
import com.quickpaas.framework.exception.WebErrorCode;
import com.quickpaas.framework.exception.WebException;
import com.quickpaas.shop.controller.BaseShopController;
import com.quickpaas.shop.system.domain.SysMenuItem;
import com.quickpaas.shop.system.service.SysMenuItemService;
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
 *
 */
@RestController
@RequestMapping("/v1/system/sysMenuItem")
@Api
@Slf4j
public class SysMenuItemController extends BaseShopController<SysMenuItem>{


    private SysMenuItemService sysMenuItemService;

    @Autowired
    public SysMenuItemController(SysMenuItemService service) {
        super(service);
        this.sysMenuItemService = service;
    }


    @PostMapping("query")
    @ApiOperation(value = "获取列表")
    @RequirePermission("system:sysMenuItem:read")
    public Mono<Result<List<SysMenuItem>>> queryMenuItemList(@RequestBody(required = false) String body) {
        Page<SysMenuItem> page = findList(body);
        return Mono.just(Result.list(page));
    }

    @PostMapping("get/{id}")
    @ApiOperation(value = "获取单条数据")
    @RequirePermission("system:sysMenuItem:read")
    public Mono<Result<SysMenuItem>> getMenuItemById(@PathVariable("id") Long id, @Valid SysMenuItem dto) {
        SysMenuItem newDto = getEntity(id, dto);
        if (newDto == null) {
            throw new WebException(WebErrorCode.ERROR_404);
        }
        return Mono.just(Result.ok(newDto));
    }


    @PostMapping(value = "create")
    @ApiOperation(value = "创建")
    @RequirePermission("system:sysMenuItem:edit")
    public Mono<Result<SysMenuItem>> createMenuItem(@Valid @RequestBody SysMenuItem dto) {
        SysMenuItem newDto = createEntity(dto);
        return Mono.just(Result.ok(newDto));
    }

    @PostMapping(value = "save")
    @ApiOperation(value = "创建")
    @RequirePermission("system:sysMenuItem:edit")
    public Mono<Result<SysMenuItem>> createOrSaveMenuItem(@Valid @RequestBody SysMenuItem dto) {
        SysMenuItem newDto = save(dto);
        return Mono.just(Result.ok(newDto));
    }

    @PutMapping("update/{id}")
    @ApiOperation(value = "更新")
    @RequirePermission("system:sysMenuItem:edit")
    public Mono<Result<SysMenuItem>> updateMenuItemById(@PathVariable("id") Long id, @RequestBody SysMenuItem dto) {
        SysMenuItem newDto = updateEntity(id, dto);
        return Mono.just(Result.ok(newDto));
    }


    @PostMapping("delete/{id}")
    @ApiOperation(value = "删除")
    @RequirePermission("system:sysMenuItem:delete")
    public Mono<Result<Boolean>> deleteMenuItemById(@PathVariable("id") Long id, @RequestBody(required = false) SysMenuItem dto) {
        Boolean res = deleteEntity(id, dto);
        return Mono.just(Result.ok(res));
    }


}
