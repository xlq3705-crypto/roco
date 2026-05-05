package com.roco.dex.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PetDTO {

    private Long id;
    private String name;
    private String petNo;
    private String mainAttr;
    private String subAttr;
    private String imageUrl;
    private Integer hp;
    private Integer attack;
    private Integer defense;
    private Integer spAttack;
    private Integer spDefense;
    private Integer speed;
    private String description;
    private String obtainMethod;
    private LocalDateTime createTime;
    private List<SkillDTO> skills;
}
