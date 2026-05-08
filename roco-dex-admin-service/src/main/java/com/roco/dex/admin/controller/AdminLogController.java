package com.roco.dex.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.roco.dex.admin.common.AdminContext;
import com.roco.dex.admin.common.PageResult;
import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.entity.LoginLog;
import com.roco.dex.admin.entity.OperationLog;
import com.roco.dex.admin.mapper.LoginLogMapper;
import com.roco.dex.admin.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/log")
@RequiredArgsConstructor
public class AdminLogController {

    private final LoginLogMapper loginLogMapper;
    private final OperationLogMapper operationLogMapper;

    @GetMapping("/login")
    public Result<PageResult<LoginLog>> loginLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        AdminContext.AdminInfo admin = AdminContext.get();

        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        // Normal admin can only see own logs
        if (!"super".equals(admin.getRole())) {
            wrapper.eq(LoginLog::getAdminId, admin.getId());
        }
        wrapper.orderByDesc(LoginLog::getId);

        Page<LoginLog> result = loginLogMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(PageResult.of(result));
    }

    @GetMapping("/operation")
    public Result<PageResult<OperationLog>> operationLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        AdminContext.AdminInfo admin = AdminContext.get();

        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        // Normal admin can only see own logs
        if (!"super".equals(admin.getRole())) {
            wrapper.eq(OperationLog::getAdminId, admin.getId());
        }
        wrapper.orderByDesc(OperationLog::getId);

        Page<OperationLog> result = operationLogMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(PageResult.of(result));
    }
}
