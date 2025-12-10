package com.room.entity.vo;

import lombok.Data;

/**
 * 登录成功返回前端
 */
@Data
public class LoginVo {

    private String username;

    private Integer userId;

    private String token;

    private String role;
}
