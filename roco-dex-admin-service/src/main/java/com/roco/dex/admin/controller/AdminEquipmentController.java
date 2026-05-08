package com.roco.dex.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.roco.dex.admin.common.AdminContext;
import com.roco.dex.admin.common.PageResult;
import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.entity.Equipment;
import com.roco.dex.admin.mapper.EquipmentMapper;
import com.roco.dex.admin.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/admin/equipment")
@RequiredArgsConstructor
public class AdminEquipmentController {

    private final EquipmentMapper equipmentMapper;
    private final OperationLogService operationLogService;

    @GetMapping("/list")
    public Result<PageResult<Equipment>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Equipment::getName, keyword)
                    .or()
                    .like(Equipment::getCategory, keyword);
        }
        wrapper.orderByDesc(Equipment::getId);
        Page<Equipment> result = equipmentMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(PageResult.of(result));
    }

    @GetMapping("/{id}")
    public Result<Equipment> getById(@PathVariable Long id) {
        Equipment equipment = equipmentMapper.selectById(id);
        return Result.success(equipment);
    }

    @PostMapping
    public Result<Equipment> create(@RequestBody Equipment equipment, HttpServletRequest request) {
        equipment.setCreateTime(OffsetDateTime.now());
        equipmentMapper.insert(equipment);

        AdminContext.AdminInfo admin = AdminContext.get();
        operationLogService.log(admin.getId(), admin.getUsername(), "装备管理", "新增",
                equipment.getId().toString(), "新增装备: " + equipment.getName(), getClientIp(request));

        return Result.success(equipment);
    }

    @PutMapping("/{id}")
    public Result<Equipment> update(@PathVariable Long id, @RequestBody Equipment equipment, HttpServletRequest request) {
        Equipment existing = equipmentMapper.selectById(id);
        if (existing == null) {
            return Result.error(404, "装备不存在");
        }

        equipment.setId(id);
        equipmentMapper.updateById(equipment);

        AdminContext.AdminInfo admin = AdminContext.get();
        operationLogService.log(admin.getId(), admin.getUsername(), "装备管理", "修改",
                id.toString(), "修改装备: " + equipment.getName(), getClientIp(request));

        return Result.success(equipment);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            return Result.error(404, "装备不存在");
        }

        equipmentMapper.deleteById(id);

        AdminContext.AdminInfo admin = AdminContext.get();
        operationLogService.log(admin.getId(), admin.getUsername(), "装备管理", "删除",
                id.toString(), "删除装备: " + equipment.getName(), getClientIp(request));

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
