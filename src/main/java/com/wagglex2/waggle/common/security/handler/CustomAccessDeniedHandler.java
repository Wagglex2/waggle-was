package com.wagglex2.waggle.common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ApiResponse<Void> apiResponse;

        // 401 - 익명 사용자
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            apiResponse = ApiResponse.error(ErrorCode.UNAUTHORIZED);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else { // 403 - 권한 부족
            apiResponse = ApiResponse.error(ErrorCode.FORBIDDEN);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
