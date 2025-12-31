package com.example.demo.core.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.entity.UserInfo;
import com.example.demo.web.mapper.UserInfoMapper;

import lombok.RequiredArgsConstructor;

/**
 * 本番用
 * DBで管理しているユーザ情報を取得するクラス
 * 
 * application.propertiesファイルに「web.security.db.auth=true」の場合に本クラスが動作する。
 */
@Component
@ConditionalOnProperty(value = "web.security.db.auth")
@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {

    private final UserInfoMapper userInfoMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 本番向けの強度をデフォルト10から11に変更（セキュリティ強化のため、より時間をかけるようにする）
        return new BCryptPasswordEncoder(11);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // ユーザ管理テーブルからユーザ情報を取得する
        UserInfo userInfo = userInfoMapper.findByUsername(username);

        if (userInfo == null) {
            // 存在しないユーザ
            throw new UsernameNotFoundException(username);
        }

        return User.builder()
                .username(username)
                .password(userInfo.getPassword())
                .roles(userInfo.getRoles().split(","))
                .build();
    }
}
