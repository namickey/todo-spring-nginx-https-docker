package com.example.demo.core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SpringSecurity6.2設定
 */
@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        // ログイン
        .formLogin(login -> login
            .defaultSuccessUrl("/", true)
            .permitAll()
        // ログアウト
        ).logout(logout -> logout
            .logoutSuccessUrl("/login")
        ).authorizeHttpRequests(authz -> authz
            // item更新画面とitem削除画面はDATA_MANAGERロールを保持しているユーザのみアクセス可能
            .requestMatchers("/WBA0401/**", "/WBA0501/**").hasRole("DATA_MANAGER")
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/actuator/**").permitAll()
            // その他画面は認証済みが必須
            .anyRequest().authenticated()
        // 開発向け、h2DBコンソール向け設定
        ).csrf(csrf -> csrf
            .ignoringRequestMatchers("/h2-console/**")
        ).headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions
                .sameOrigin())
        );
        return http.build();
    }
}
