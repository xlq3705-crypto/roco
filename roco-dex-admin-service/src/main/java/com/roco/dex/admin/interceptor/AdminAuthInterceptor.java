package com.roco.dex.admin.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roco.dex.admin.common.AdminContext;
import com.roco.dex.admin.common.JwtUtil;
import com.roco.dex.admin.common.Result;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Allow OPTIONS requests for CORS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, 401, "未登录或token无效");
            return false;
        }

        String token = authHeader.substring(7);
        try {
            if (!jwtUtil.isTokenValid(token)) {
                sendError(response, 401, "token已过期");
                return false;
            }

            Claims claims = jwtUtil.parseToken(token);
            Long adminId = Long.parseLong(claims.getSubject());
            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);

            AdminContext.AdminInfo adminInfo = new AdminContext.AdminInfo();
            adminInfo.setId(adminId);
            adminInfo.setUsername(username);
            adminInfo.setRole(role);
            AdminContext.set(adminInfo);

            return true;
        } catch (Exception e) {
            sendError(response, 401, "token验证失败");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AdminContext.clear();
    }

    private void sendError(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        Result<?> result = Result.error(code, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
