package com.roco.dex.controller;

import com.roco.dex.common.PageResult;
import com.roco.dex.common.Result;
import com.roco.dex.entity.Equipment;
import com.roco.dex.service.EquipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "装备管理")
@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Operation(summary = "装备列表")
    @GetMapping("/list")
    public Result<PageResult<Equipment>> list(
            @Parameter(description = "分类筛选") @RequestParam(required = false) String category,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        return Result.success(PageResult.of(equipmentService.list(category, page, size)));
    }

    @Operation(summary = "装备详情")
    @GetMapping("/{id}")
    public Result<Equipment> detail(@PathVariable Long id) {
        Equipment equipment = equipmentService.getDetail(id);
        if (equipment == null) {
            return Result.error(404, "装备不存在");
        }
        return Result.success(equipment);
    }

    @Operation(summary = "搜索装备")
    @GetMapping("/search")
    public Result<PageResult<Equipment>> search(
            @Parameter(description = "关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        return Result.success(PageResult.of(equipmentService.search(keyword, page, size)));
    }
}
