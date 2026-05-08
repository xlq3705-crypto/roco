package com.roco.dex.admin.service;

import com.roco.dex.admin.dto.AdminLoginDTO;
import com.roco.dex.admin.dto.AdminVO;

public interface AdminAuthService {

    AdminVO login(AdminLoginDTO dto, String ip, String userAgent);

    AdminVO getCurrentAdmin();
}
