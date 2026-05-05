package com.roco.dex.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@TableName("roc_pet")
public class Pet {

    @TableId(type = IdType.AUTO)
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
    private OffsetDateTime createTime;
}
