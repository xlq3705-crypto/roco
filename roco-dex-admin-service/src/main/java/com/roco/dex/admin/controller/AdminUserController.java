package com.roco.dex.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.roco.dex.admin.common.AdminContext;
import com.roco.dex.admin.common.PageResult;
import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.entity.User;
import com.roco.dex.admin.mapper.UserMapper;
import com.roco.dex.admin.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserMapper userMapper;
    private final OperationLogService operationLogService;

    @GetMapping("/list")
    public Result<PageResult<User>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword)
                    .or()
                    .like(User::getNickname, keyword);
        }
        wrapper.orderByDesc(User::getId);
        Page<User> result = userMapper.selectPage(new Page<>(page, size), wrapper);

        // Clear passwords
        List<User> records = result.getRecords();
        for (User user : records) {
            user.setPassword(null);
        }

        return Result.success(PageResult.of(result));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody User user, HttpServletRequest request) {
        User existing = userMapper.selectById(id);
        if (existing == null) {
            return Result.error(404, "用户不存在");
        }

        existing.setPassword(null);
        existing.setAvatarUrl(null);
        // Only update nickname if provided
        if (user.getNickname() != null) {
            existing.setNickname(user.getNickname());
        }
        userMapper.updateById(existing);

        AdminContext.AdminInfo admin = AdminContext.get();
        operationLogService.log(admin.getId(), admin.getUsername(), "用户管理", "修改",
                id.toString(), "修改用户: " + existing.getUsername(), getClientIp(request));

        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        userMapper.deleteById(id);

        AdminContext.AdminInfo admin = AdminContext.get();
        operationLogService.log(admin.getId(), admin.getUsername(), "用户管理", "删除",
                id.toString(), "删除用户: " + user.getUsername(), getClientIp(request));

        return Result.success();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
