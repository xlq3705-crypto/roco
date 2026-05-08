package com.roco.dex.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.roco.dex.admin.common.AdminContext;
import com.roco.dex.admin.common.PageResult;
import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.dto.PetAdminDTO;
import com.roco.dex.admin.entity.Pet;
import com.roco.dex.admin.entity.PetSkill;
import com.roco.dex.admin.mapper.PetMapper;
import com.roco.dex.admin.mapper.PetSkillMapper;
import com.roco.dex.admin.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/pet")
@RequiredArgsConstructor
public class AdminPetController {

    private final PetMapper petMapper;
    private final PetSkillMapper petSkillMapper;
    private final OperationLogService operationLogService;

    @GetMapping("/list")
    public Result<PageResult<Pet>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Pet::getName, keyword)
                    .or()
                    .like(Pet::getPetNo, keyword);
        }
        wrapper.orderByDesc(Pet::getId);
        Page<Pet> result = petMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(PageResult.of(result));
    }

    @GetMapping("/{id}")
    public Result<Pet> getById(@PathVariable Long id) {
        Pet pet = petMapper.selectById(id);
        return Result.success(pet);
    }

    @PostMapping
    @Transactional
    public Result<Pet> create(@RequestBody PetAdminDTO dto, HttpServletRequest request) {
        Pet pet = new Pet();
        pet.setName(dto.getName());
        pet.setPetNo(dto.getPetNo());
        pet.setMainAttr(dto.getMainAttr());
        pet.setSubAttr(dto.getSubAttr());
        pet.setImageUrl(dto.getImageUrl());
        pet.setHp(dto.getHp());
        pet.setAttack(dto.getAttack());
        pet.setDefense(dto.getDefense());
        pet.setSpAttack(dto.getSpAttack());
        pet.setSpDefense(dto.getSpDefense());
        pet.setSpeed(dto.getSpeed());
        pet.setDescription(dto.getDescription());
        pet.setObtainMethod(dto.getObtainMethod());
        pet.setCreateTime(OffsetDateTime.now());
        petMapper.insert(pet);

        // Save pet-skill relations
        if (dto.getSkillIds() != null && !dto.getSkillIds().isEmpty()) {
            for (Long skillId : dto.getSkillIds()) {
                PetSkill ps = new PetSkill();
                ps.setPetId(pet.getId());
                ps.setSkillId(skillId);
                petSkillMapper.insert(ps);
            }
        }

        // Log operation
        AdminContext.AdminInfo admin = AdminContext.get();
        operationLogService.log(admin.getId(), admin.getUsername(), "宠物管理", "新增",
                pet.getId().toString(), "新增宠物: " + pet.getName(), getClientIp(request));

        return Result.success(pet);
    }

    @PutMapping("/{id}")
    @Transactional
    public Result<Pet> update(@PathVariable Long id, @RequestBody PetAdminDTO dto, HttpServletRequest request) {
        Pet pet = petMapper.selectById(id);
        if (pet == null) {
            return Result.error(404, "宠物不存在");
        }

        pet.setName(dto.getName());
        pet.setPetNo(dto.getPetNo());
        pet.setMainAttr(dto.getMainAttr());
        pet.setSubAttr(dto.getSubAttr());
        pet.setImageUrl(dto.getImageUrl());
        pet.setHp(dto.getHp());
        pet.setAttack(dto.getAttack());
        pet.setDefense(dto.getDefense());
        pet.setSpAttack(dto.getSpAttack());
        pet.setSpDefense(dto.getSpDefense());
        pet.setSpeed(dto.getSpeed());
        pet.setDescription(dto.getDescription());
        pet.setObtainMethod(dto.getObtainMethod());
        petMapper.updateById(pet);

        // Update pet-skill relations
        if (dto.getSkillIds() != null) {
            petSkillMapper.delete(new LambdaQueryWrapper<PetSkill>().eq(PetSkill::getPetId, id));
            for (Long skillId : dto.getSkillIds()) {
                PetSkill ps = new PetSkill();
                ps.setPetId(id);
                ps.setSkillId(skillId);
                petSkillMapper.insert(ps);
            }
        }

        // Log operation
        AdminContext.AdminInfo admin = AdminContext.get();
        operationLogService.log(admin.getId(), admin.getUsername(), "宠物管理", "修改",
                id.toString(), "修改宠物: " + pet.getName(), getClientIp(request));

        return Result.success(pet);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Pet pet = petMapper.selectById(id);
        if (pet == null) {
            return Result.error(404, "宠物不存在");
        }

        petMapper.deleteById(id);
        petSkillMapper.delete(new LambdaQueryWrapper<PetSkill>().eq(PetSkill::getPetId, id));

        // Log operation
        AdminContext.AdminInfo admin = AdminContext.get();
        operationLogService.log(admin.getId(), admin.getUsername(), "宠物管理", "删除",
                id.toString(), "删除宠物: " + pet.getName(), getClientIp(request));

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
