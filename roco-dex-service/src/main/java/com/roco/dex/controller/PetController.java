package com.roco.dex.controller;

import com.roco.dex.common.PageResult;
import com.roco.dex.common.Result;
import com.roco.dex.dto.PetDTO;
import com.roco.dex.entity.Pet;
import com.roco.dex.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "宠物管理")
@RestController
@RequestMapping("/api/pet")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @Operation(summary = "宠物列表")
    @GetMapping("/list")
    public Result<PageResult<Pet>> list(
            @Parameter(description = "主属性筛选") @RequestParam(required = false) String attr,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        return Result.success(PageResult.of(petService.list(attr, page, size)));
    }

    @Operation(summary = "宠物详情")
    @GetMapping("/{id}")
    public Result<PetDTO> detail(@PathVariable Long id) {
        PetDTO pet = petService.getDetail(id);
        if (pet == null) {
            return Result.error(404, "宠物不存在");
        }
        return Result.success(pet);
    }

    @Operation(summary = "搜索宠物")
    @GetMapping("/search")
    public Result<PageResult<Pet>> search(
            @Parameter(description = "关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        return Result.success(PageResult.of(petService.search(keyword, page, size)));
    }
}
