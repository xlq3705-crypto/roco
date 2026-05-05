package com.roco.dex.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.roco.dex.dto.PetDTO;
import com.roco.dex.dto.SkillDTO;
import com.roco.dex.entity.Pet;
import com.roco.dex.entity.PetSkill;
import com.roco.dex.entity.Skill;
import com.roco.dex.mapper.PetMapper;
import com.roco.dex.mapper.PetSkillMapper;
import com.roco.dex.mapper.SkillMapper;
import com.roco.dex.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetMapper petMapper;
    private final PetSkillMapper petSkillMapper;
    private final SkillMapper skillMapper;

    @Override
    @Cacheable(value = "petList", key = "#attr + ':' + #page + ':' + #size")
    public IPage<Pet> list(String attr, int page, int size) {
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(attr)) {
            wrapper.eq(Pet::getMainAttr, attr);
        }
        wrapper.orderByDesc(Pet::getId);
        return petMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Cacheable(value = "petDetail", key = "#id")
    public PetDTO getDetail(Long id) {
        Pet pet = petMapper.selectById(id);
        if (pet == null) {
            return null;
        }

        PetDTO dto = new PetDTO();
        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setPetNo(pet.getPetNo());
        dto.setMainAttr(pet.getMainAttr());
        dto.setSubAttr(pet.getSubAttr());
        dto.setImageUrl(pet.getImageUrl());
        dto.setHp(pet.getHp());
        dto.setAttack(pet.getAttack());
        dto.setDefense(pet.getDefense());
        dto.setSpAttack(pet.getSpAttack());
        dto.setSpDefense(pet.getSpDefense());
        dto.setSpeed(pet.getSpeed());
        dto.setDescription(pet.getDescription());
        dto.setObtainMethod(pet.getObtainMethod());
        dto.setCreateTime(pet.getCreateTime());

        // 查询宠物技能
        List<PetSkill> petSkills = petSkillMapper.selectList(
                new LambdaQueryWrapper<PetSkill>().eq(PetSkill::getPetId, id));
        if (!petSkills.isEmpty()) {
            List<Long> skillIds = petSkills.stream().map(PetSkill::getSkillId).toList();
            List<Skill> skills = skillMapper.selectBatchIds(skillIds);

            List<SkillDTO> skillDTOs = skills.stream().map(skill -> {
                SkillDTO skillDTO = new SkillDTO();
                skillDTO.setId(skill.getId());
                skillDTO.setName(skill.getName());
                skillDTO.setAttr(skill.getAttr());
                skillDTO.setType(skill.getType());
                skillDTO.setPower(skill.getPower());
                skillDTO.setPp(skill.getPp());
                skillDTO.setAccuracy(skill.getAccuracy());
                skillDTO.setDescription(skill.getDescription());
                skillDTO.setImageUrl(skill.getImageUrl());
                petSkills.stream()
                        .filter(ps -> ps.getSkillId().equals(skill.getId()))
                        .findFirst()
                        .ifPresent(ps -> skillDTO.setLearnLevel(ps.getLearnLevel()));
                return skillDTO;
            }).toList();
            dto.setSkills(skillDTOs);
        }

        return dto;
    }

    @Override
    public IPage<Pet> search(String keyword, int page, int size) {
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Pet::getName, keyword)
                .or()
                .like(Pet::getPetNo, keyword);
        wrapper.orderByDesc(Pet::getId);
        return petMapper.selectPage(new Page<>(page, size), wrapper);
    }
}
