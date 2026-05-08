package com.roco.dex.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@TableName("roc_operation_log")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long adminId;
    private String adminUsername;
    private String module;
    private String action;
    private String targetId;
    private String detail;
    private String ip;
    private OffsetDateTime createTime;
}
