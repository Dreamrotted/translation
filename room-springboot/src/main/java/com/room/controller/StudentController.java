package com.room.controller;

import com.github.pagehelper.PageInfo;
import com.room.common.Result;
import com.room.entity.pojo.Student;
import com.room.service.StudentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;


/**
 * 学生管理
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Resource
    private StudentService studentService;

    /**
     * 分页查询
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return Result
     */
    @GetMapping("/page")
    public Result<PageInfo<Student>> page(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        PageInfo<Student> page = studentService.page(pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 添加
     * @param student 学生信息
     * @return Result
     */
    @PostMapping("/add")
    public Result<Object> add(@RequestBody Student student) {
        studentService.add(student);
        return Result.success("添加成功");
    }

    /**
     * 修改
     * @param student 学生信息
     * @return Result
     */
    @PutMapping("/update")
    public Result<Object> update(@RequestBody Student student) {
        studentService.update(student);
        return Result.success();
    }

    /**
     * 删除
     * @param id id
     * @return Result
     */
    @DeleteMapping("/delete/{id}")
    public Result<Object> delete(@PathVariable Integer id) {
        studentService.delete(id);
        return Result.success();
    }

    /**
     * 根据id查询学生信息
     * @param id id
     * @return Result
     */
    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Integer id) {
        Student student = studentService.getById(id);
        // 不返回密码
        if (student != null) {
            student.setPassword(null);
        }
        return Result.success(student);
    }

    /**
     * 根据学号查询学生信息
     * @param studentNumber 学号
     * @return Result
     */
    @GetMapping("/getByNumber")
    public Result<Student> getByNumber(@RequestParam Integer studentNumber) {
        Student student = studentService.getByStudentNumber(studentNumber);
        if (student != null) {
            student.setPassword(null);
        }
        return Result.success(student);
    }

    /**
     * 更新基本信息（不包含密码）
     * @param student 学生信息
     * @return Result
     */
    @PutMapping("/updateInfo")
    public Result<Object> updateInfo(@RequestBody Student student) {
        studentService.updateInfo(student);
        return Result.success("修改成功");
    }

    /**
     * 修改密码
     * @param params 参数
     * @return Result
     */
    @PutMapping("/changePassword")
    public Result<Object> changePassword(@RequestBody Map<String, Object> params) {
        Integer studentId = (Integer) params.get("studentId");
        String oldPassword = (String) params.get("oldPassword");
        String newPassword = (String) params.get("newPassword");
        
        studentService.changePassword(studentId, oldPassword, newPassword);
        return Result.success("密码修改成功");
    }
}
