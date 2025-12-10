package com.room.controller;

import com.github.pagehelper.PageInfo;
import com.room.common.Result;
import com.room.entity.pojo.ViolationRecord;
import com.room.service.ViolationRecordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 违规记录管理
 */
@RestController
@RequestMapping("/violation")
public class ViolationRecordController {

    @Resource
    private ViolationRecordService violationRecordService;

    /**
     * 分页查询违规记录
     */
    @GetMapping("/page")
    public Result<PageInfo<ViolationRecord>> page(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "studentNumber", required = false) Integer studentNumber,
            @RequestParam(value = "violationType", required = false) String violationType,
            @RequestParam(value = "recordType", required = false) Integer recordType,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ) {
        return Result.success(violationRecordService.page(pageNum, pageSize, studentNumber, violationType, recordType, status, startDate, endDate));
    }

    /**
     * 管理员手动添加违规记录
     */
    @PostMapping("/add")
    public Result<Object> add(@RequestBody ViolationRecord violationRecord) {
        violationRecordService.add(violationRecord);
        return Result.success("添加成功");
    }

    /**
     * 更新违规记录
     */
    @PutMapping("/update")
    public Result<Object> update(@RequestBody ViolationRecord violationRecord) {
        violationRecordService.update(violationRecord);
        return Result.success("更新成功");
    }

    /**
     * 删除违规记录
     */
    @DeleteMapping("/delete/{id}")
    public Result<Object> delete(@PathVariable Integer id) {
        violationRecordService.deleteById(id);
        return Result.success("删除成功");
    }

    /**
     * 获取学生本月违规次数
     */
    @GetMapping("/monthCount/{studentId}")
    public Result<Integer> getMonthViolationCount(@PathVariable Integer studentId) {
        return Result.success(violationRecordService.getMonthViolationCount(studentId));
    }
}
