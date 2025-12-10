package com.room.entity.pojo;

import lombok.Data;

/**
 * 学生表
 */
@Data
public class Student {

    private Integer id;

    //学号
    private Integer studentNumber;

    //姓名
    private String studentName;

    //性别(1男 0女)
    private Integer gender;

    //密码
    private String password;

    //专业名称
    private String majorName;
}
