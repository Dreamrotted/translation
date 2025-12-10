package com.room.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.room.entity.pojo.Student;
import com.room.exception.BusinessException;
import com.room.mapper.StudentMapper;
import com.room.service.StudentService;
import com.room.utils.Utils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Resource
    private StudentMapper studentMapper;


    @Override
    public Student login(String account, String password) {
        Student dbStudent = studentMapper.getByStudentNumber(Integer.valueOf(account));
        if (dbStudent == null) {
            throw new BusinessException("账号不存在");
        }
        if (!Utils.checkPassword(password, dbStudent.getPassword())) {
            throw new BusinessException("密码错误");
        }
        return dbStudent;
    }

    @Override
    public PageInfo<Student> page(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Student> list = studentMapper.list();
        return new PageInfo<>(list);
    }

    @Override
    public void add(Student student) {
        if (studentMapper.getByStudentNumber(student.getStudentNumber()) != null) {
            throw new BusinessException("该学号已存在");
        }
        String password = student.getPassword();
        password = Utils.encryptPassword(password);
        student.setPassword(password);
        studentMapper.add(student);
    }

    @Override
    public void delete(Integer id) {
        studentMapper.deleteById(id);
    }

    @Override
    public void update(Student student) {
        Student dbStudent = studentMapper.getById(student.getId());
        if (student.getStudentNumber() != null && !student.getStudentNumber().equals(dbStudent.getStudentNumber())){
            if (studentMapper.getByStudentNumber(student.getStudentNumber()) != null) {
                throw new BusinessException("该学号已存在");
            }
        }
        if (student.getPassword() != null && !student.getPassword().equals(dbStudent.getPassword())){
            String password = student.getPassword();
            password = Utils.encryptPassword(password);
            student.setPassword(password);
        }
        studentMapper.update(student);
    }

    @Override
    public Student getById(Integer id) {
        return studentMapper.getById(id);
    }

    @Override
    public Student getByStudentNumber(Integer studentNumber) {
        return studentMapper.getByStudentNumber(studentNumber);
    }

    @Override
    public void updateInfo(Student student) {
        // 更新基本信息，不包含密码
        Student dbStudent = studentMapper.getById(student.getId());
        if (dbStudent == null) {
            throw new BusinessException("学生不存在");
        }
        
        // 检查学号是否重复
        if (student.getStudentNumber() != null && !student.getStudentNumber().equals(dbStudent.getStudentNumber())) {
            if (studentMapper.getByStudentNumber(student.getStudentNumber()) != null) {
                throw new BusinessException("该学号已存在");
            }
        }
        
        // 只更新基本信息，不更新密码
        student.setPassword(null);
        studentMapper.update(student);
    }

    @Override
    public void changePassword(Integer studentId, String oldPassword, String newPassword) {
        // 获取学生信息
        Student student = studentMapper.getById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        // 验证原密码
        if (!Utils.checkPassword(oldPassword, student.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        
        // 加密新密码
        String encryptedPassword = Utils.encryptPassword(newPassword);
        
        // 更新密码
        Student updateStudent = new Student();
        updateStudent.setId(studentId);
        updateStudent.setPassword(encryptedPassword);
        studentMapper.update(updateStudent);
    }
}
