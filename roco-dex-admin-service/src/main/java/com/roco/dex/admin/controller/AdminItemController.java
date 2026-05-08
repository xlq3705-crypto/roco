package com.roco.dex.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.roco.dex.admin.common.AdminContext;
import com.roco.dex.admin.common.PageResult;
import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.entity.Item;
import com.roco.dex.admin.mapper.ItemMapper;
import com.roco.dex.admin.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/admin/item")
@RequiredArgsConstructor
public class AdminItemController {

    private final ItemMapper itemMapper;
    private final OperationLogService operationLogService;

    @GetMapping("/list")
    public Result<PageResult<Item>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Item::getName, keyword)
                    .or()
                    .like(Item::getCategory, keyword);
        }
        wrapper.orderByDesc(Item::getId);
        Page<Item> result = itemMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(PageResult.of(result));
    }

    @GetMapping("/{id}")
    public Result<Item> getById(@PathVariable Long id) {
        Item item = itemMapper.selectById(id);
        return Result.success(item);
    }

    @PostMapping
    public Result<Item> create(@RequestBody Item item, HttpServletRequest request) {
        item.setCreateTime(OffsetDateTime.now());
        itemMapper.insert(item);

        AdminContext.AdminInfo admin = AdminContext.get();
        operationLogService.log(admin.getId(), admin.getUsername(), "道具管理", "新增",
                item.getId().toString(), "新增道具: " + item.getName(), getClientIp(request));

        return Result.success(item);
    }

    @PutMapping("/{id}")
    public Result<Item> update(@PathVariable Long id, @RequestBody Item item, HttpServletRequest request) {
        Item existing = itemMapper.selectById(id);
        if (existing == null) {
            return Result.error(404, "道具不存在");
        }

        item.setId(id);
        itemMapper.updateById(item);

        AdminContext.AdminInfo admin = AdminContext.get();
        operationLogService.log(admin.getId(), admin.getUsername(), "道具管理", "修改",
                id.toString(), "修改道具: " + item.getName(), getClientIp(request));

        return Result.success(item);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Item item = itemMapper.selectById(id);
        if (item == null) {
            return Result.error(404, "道具不存在");
        }

        itemMapper.deleteById(id);

        AdminContext.AdminInfo admin = AdminContext.get();
        operationLogService.log(admin.getId(), admin.getUsername(), "道具管理", "删除",
                id.toString(), "删除道具: " + item.getName(), getClientIp(request));

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
