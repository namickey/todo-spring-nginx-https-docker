package com.example.demo.entity;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserInfo {

    private String username;
    private String roles;
    private String password;
    private String email;
    private LocalDate registDate;
    private Integer versionNo;

}
