package com.roco.dex.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("roc_skill")
public class Skill {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String attr;
    private String type;
    private Integer power;
    private Integer pp;
    private Integer accuracy;
    private String description;
    private String imageUrl;
    private LocalDateTime createTime;
}
