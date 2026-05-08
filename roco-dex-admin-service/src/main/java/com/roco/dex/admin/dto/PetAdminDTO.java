package com.roco.dex.admin.dto;

import lombok.Data;

import java.util.List;

@Data
public class PetAdminDTO {

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
    private List<Long> skillIds;
}
