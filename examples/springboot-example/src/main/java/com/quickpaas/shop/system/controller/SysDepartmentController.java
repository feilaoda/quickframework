package com.quickpaas.shop.system.controller;


import com.quickpaas.framework.domain.Page;
import com.quickpaas.framework.annotation.RequirePermission;
import com.quickpaas.framework.domain.Result;
import com.quickpaas.framework.exception.WebException;
import com.quickpaas.shop.controller.BaseShopController;
import com.quickpaas.shop.system.domain.SysDepartment;
import com.quickpaas.shop.system.service.SysDepartmentService;
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
@RequestMapping("/v1/system/sysDepartment")
@Api
@Slf4j
public class SysDepartmentController extends BaseShopController<SysDepartment> {


    private final SysDepartmentService sysDepartmentService;

    @Autowired
    public SysDepartmentController(SysDepartmentService sysDepartmentService) {
        super(sysDepartmentService);
        this.sysDepartmentService = sysDepartmentService;
    }

    @PostMapping("query")
    @ApiOperation(value = "获取列表", httpMethod = "POST")
    @RequirePermission("system:sysDepartment:read")
    public Mono<Result<List<SysDepartment>>> queryDepartmentList(@RequestBody(required = false) String body) {
        Page<SysDepartment> page = findList(body);
        return Mono.just(Result.list(page));
    }

    @PostMapping("get/{id}")
    @ApiOperation(value = "获取单条数据", httpMethod = "POST")
    @RequirePermission("system:sysDepartment:read")
    public Mono<Result<SysDepartment>> getDepartmentById(@PathVariable("id") Long id, @RequestBody(required = false) String body) {
        SysDepartment newDto = getEntity(id, body);
        if (newDto == null) {
            throw new WebException(ERROR_404);
        }
        return Mono.just(Result.ok(newDto));
    }


    @PostMapping(value = "save")
    @ApiOperation(value = "创建")
    @RequirePermission("system:sysDepartment:edit")
    public Mono<Result<SysDepartment>> createOrSaveDepartment(@Valid @RequestBody SysDepartment dto) {
        checkUnique( "部门名称", "name", dto.getName());
        if (dto.tid() == null) {
            SysDepartment newDto = createEntity(dto);
            return Mono.just(Result.ok(newDto));
        } else {
            SysDepartment newDto = updateEntity(dto.tid(), dto);
            return Mono.just(Result.ok(newDto));
        }
    }

    @PostMapping(value = "create")
    @ApiOperation(value = "创建")
    @RequirePermission("system:sysDepartment:edit")
    public Mono<Result<SysDepartment>> createDepartment(@Valid @RequestBody SysDepartment dto) {
        SysDepartment newDto = createEntity(dto);
        return Mono.just(Result.ok(newDto));
    }

    @PutMapping("update/{id}")
    @ApiOperation(value = "更新")
    @RequirePermission("system:sysDepartment:edit")
    public Mono<Result<SysDepartment>> updateDepartmentById(@PathVariable("id") Long id, @RequestBody SysDepartment dto) {
        SysDepartment newDto = updateEntity(id, dto);
        return Mono.just(Result.ok(newDto));
    }


    @PostMapping("delete/{id}")
    @ApiOperation(value = "删除")
    @RequirePermission("system:sysDepartment:delete")
    public Mono<Result<Boolean>> deleteDepartmentById(@PathVariable("id") Long id, @RequestBody(required = false) SysDepartment dto) {
        Boolean res = deleteEntity(id, dto);
        return Mono.just(Result.ok(res));
    }

}
