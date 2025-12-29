package org.example.boxes.dto;

import lombok.Data;

@Data
public class LoginRequest {
    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 登录密码
     */
    private String userPassword;
}