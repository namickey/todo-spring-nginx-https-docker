package com.example.demo.web.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.entity.UserInfo;

@Mapper
public interface UserInfoMapper {

    /**
     * 1件検索
     * @param username ユーザ名
     * @return UserInfo
     */
    UserInfo findByUsername(String username);
}
