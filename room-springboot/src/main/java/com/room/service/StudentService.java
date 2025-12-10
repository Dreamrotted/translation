package com.room.service;

import com.github.pagehelper.PageInfo;
import com.room.entity.pojo.Student;

public interface StudentService {

    Student login(String account, String password);

    PageInfo<Student> page(Integer pageNum, Integer pageSize);

    void add(Student student);

    void delete(Integer id);

    void update(Student student);

    Student getById(Integer id);

    /**
     * 根据学号查询学生
     */
    Student getByStudentNumber(Integer studentNumber);

    /**
     * 更新基本信息（不包含密码）
     */
    void updateInfo(Student student);

    /**
     * 修改密码
     */
    void changePassword(Integer studentId, String oldPassword, String newPassword);
}
