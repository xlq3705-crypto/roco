package com.roco.dex.controller;

import com.roco.dex.common.PageResult;
import com.roco.dex.common.Result;
import com.roco.dex.entity.Item;
import com.roco.dex.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "道具管理")
@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @Operation(summary = "道具列表")
    @GetMapping("/list")
    public Result<PageResult<Item>> list(
            @Parameter(description = "分类筛选") @RequestParam(required = false) String category,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        return Result.success(PageResult.of(itemService.list(category, page, size)));
    }

    @Operation(summary = "道具详情")
    @GetMapping("/{id}")
    public Result<Item> detail(@PathVariable Long id) {
        Item item = itemService.getDetail(id);
        if (item == null) {
            return Result.error(404, "道具不存在");
        }
        return Result.success(item);
    }

    @Operation(summary = "搜索道具")
    @GetMapping("/search")
    public Result<PageResult<Item>> search(
            @Parameter(description = "关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        return Result.success(PageResult.of(itemService.search(keyword, page, size)));
    }
}
