package com.roco.dex.admin.service;

import com.roco.dex.admin.entity.OperationLog;
import com.roco.dex.admin.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogMapper operationLogMapper;

    public void log(Long adminId, String adminUsername, String module, String action, String targetId, String detail, String ip) {
        OperationLog log = new OperationLog();
        log.setAdminId(adminId);
        log.setAdminUsername(adminUsername);
        log.setModule(module);
        log.setAction(action);
        log.setTargetId(targetId);
        log.setDetail(detail);
        log.setIp(ip);
        log.setCreateTime(OffsetDateTime.now());
        operationLogMapper.insert(log);
    }
}
