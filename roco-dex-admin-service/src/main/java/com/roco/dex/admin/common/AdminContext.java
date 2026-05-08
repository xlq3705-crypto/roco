package com.roco.dex.admin.common;

import lombok.Data;

public class AdminContext {

    private static final ThreadLocal<AdminInfo> CONTEXT = new ThreadLocal<>();

    public static void set(AdminInfo adminInfo) {
        CONTEXT.set(adminInfo);
    }

    public static AdminInfo get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }

    @Data
    public static class AdminInfo {
        private Long id;
        private String username;
        private String role;
    }
}
