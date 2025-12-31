package com.example.demo.core.logs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * 認証済みユーザ名を request 属性 userId に設定。
 * logback-access パターンで %{userId}r 参照。
 */
@Component
@Order(Integer.MAX_VALUE - 1) // 認証処理が終わった後なら順番は厳密でなくても可
public class AccessUserAttributeFilter extends OncePerRequestFilter {

    private static final String[] CLIENT_IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "X-Real-IP"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()
                    && !"anonymousUser".equals(auth.getName())) {
                request.setAttribute("userId", auth.getName());
                MDC.put("userId", String.format("%-7s", auth.getName()));
            }

            String remoteIp = resolveClientIp(request);
            if (!remoteIp.isEmpty()) {
                MDC.put("remoteIp", remoteIp);
            }
            
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("userId");
            MDC.remove("remoteIp");
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        for (String header : CLIENT_IP_HEADER_CANDIDATES) {
            String headerValue = request.getHeader(header);
            if (headerValue != null && !headerValue.isBlank() && !"unknown".equalsIgnoreCase(headerValue)) {
                if ("X-Forwarded-For".equalsIgnoreCase(header)) {
                    return parseFirstForwardedIp(headerValue);
                }
                return headerValue.trim();
            }
        }
        return Optional.ofNullable(request.getRemoteAddr()).orElse("");
    }

    private String parseFirstForwardedIp(String forwardedForHeader) {
        int commaIndex = forwardedForHeader.indexOf(',');
        if (commaIndex == -1) {
            return forwardedForHeader.trim();
        }
        return forwardedForHeader.substring(0, commaIndex).trim();
    }
}