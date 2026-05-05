package com.roco.dex.dto;

import lombok.Data;

@Data
public class SkillDTO {

    private Long id;
    private String name;
    private String attr;
    private String type;
    private Integer power;
    private Integer pp;
    private Integer accuracy;
    private String description;
    private String imageUrl;
    private Integer learnLevel;
}
