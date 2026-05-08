package com.roco.dex.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@TableName("roc_login_log")
public class LoginLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long adminId;
    private String username;
    private String ip;
    private String userAgent;
    private Integer status;
    private OffsetDateTime createTime;
}
