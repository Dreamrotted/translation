package com.room.controller;

import com.room.common.Result;
import com.room.entity.dto.UserDto;
import com.room.entity.pojo.Admin;
import com.room.entity.pojo.Student;
import com.room.exception.BusinessException;
import com.room.service.AdminService;
import com.room.service.StudentService;
import com.room.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录
 */
@RestController
@Slf4j
public class LoginController {

    @Resource
    private StudentService studentService;

    @Resource
    private AdminService adminService;

    /**
     * 学生注册
     */
    @PostMapping("/register")
    public Result<Object> register(@RequestBody Student student) {
        // 参数验证
        if (student.getStudentNumber() == null || student.getStudentNumber() <= 0) {
            return Result.failed("学号不能为空且必须大于0");
        }
        if (student.getStudentName() == null || student.getStudentName().trim().isEmpty()) {
            return Result.failed("姓名不能为空");
        }
        if (student.getStudentName().length() > 50) {
            return Result.failed("姓名长度不能超过50个字符");
        }
        if (student.getPassword() == null || student.getPassword().isEmpty()) {
            return Result.failed("密码不能为空");
        }
        if (student.getPassword().length() < 6 || student.getPassword().length() > 20) {
            return Result.failed("密码长度必须在6-20个字符之间");
        }
        if (student.getGender() == null || (student.getGender() != 0 && student.getGender() != 1)) {
            return Result.failed("性别必须为0或1");
        }
        if (student.getMajorName() == null || student.getMajorName().trim().isEmpty()) {
            return Result.failed("专业不能为空");
        }
        if (student.getMajorName().length() > 100) {
            return Result.failed("专业名称长度不能超过100个字符");
        }
        
        try {
            // 检查学号是否已存在
            Student existingStudent = studentService.getByStudentNumber(student.getStudentNumber());
            if (existingStudent != null) {
                return Result.failed("该学号已被注册");
            }
            studentService.add(student);
            return Result.success("注册成功");
        } catch (BusinessException e) {
            log.error("注册失败: {}", e.getMessage());
            return Result.failed(e.getMessage());
        } catch (Exception e) {
            log.error("注册异常: {}", e.getMessage());
            return Result.failed("注册失败，请稍后重试");
        }
    }

    /**
     * 学生：学号密码登录
     * 管理员：账号密码登录
     */
    @PostMapping("/login")
    public Result<Object> userLogin(@RequestBody UserDto userDto) {
        String account = userDto.getAccount();
        if (account == null || account.isEmpty()) {
            return Result.failed("账号不能为空");
        }
        String password = userDto.getPassword();
        if (password == null || password.isEmpty()) {
            return Result.failed("密码不能为空");
        }
        String role = userDto.getRole();
        if (role == null || role.isEmpty()) {
            return Result.failed("角色不能为空");
        }
        Map<String, Object> map = new HashMap<>();
        try {
            if (role.equals("student")) {
                Student student = studentService.login(account, password);
                map.put("user", student);
                map.put("role", role);
                String token = JwtUtils.generateToken(account);
                map.put("token", token);
                return Result.success(map);
            } else if (role.equals("admin")) {
                Admin admin = adminService.login(account, password);
                map.put("user", admin);
                map.put("role", role);
                String token = JwtUtils.generateToken(account);
                map.put("token", token);
                return Result.success(map);
            } else {
                return Result.failed("无效的角色");
            }
        } catch (BusinessException e) {
            log.error("登录失败: {}", e.getMessage());
            return Result.failed(e.getMessage());
        }
    }


}
