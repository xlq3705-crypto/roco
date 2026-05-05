package com.roco.dex.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("roc_pet_skill")
public class PetSkill {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long petId;
    private Long skillId;
    private Integer learnLevel;
}
