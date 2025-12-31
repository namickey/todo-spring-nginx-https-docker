package com.example.demo.core.logs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * HTTP セッションIDを MDC(jsessionId) に格納。
 * ログパターンの %X{jsessionId} で出力される。
 */
@Component
@Order(Integer.MIN_VALUE + 50) // 実行順序は早めを指定
public class SessionMdcFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 既存セッションを取得 (false)。
            var session = request.getSession(false);
            if (session != null) {
                MDC.put("jsessionId", session.getId());
            }
            filterChain.doFilter(request, response);
        } finally {
            // スレッド再利用対策で MDC をクリア
            MDC.remove("jsessionId");
        }
    }
}