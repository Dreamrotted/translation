package com.room.controller;

import com.github.pagehelper.PageInfo;
import com.room.common.Result;
import com.room.entity.pojo.Feedback;
import com.room.service.FeedbackService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 投诉与评价管理
 */
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Resource
    private FeedbackService feedbackService;

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<PageInfo<Feedback>> page(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "studentNumber", required = false) Integer studentNumber,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ) {
        return Result.success(feedbackService.page(pageNum, pageSize, studentNumber, type, status, startDate, endDate));
    }

    /**
     * 提交反馈
     */
    @PostMapping("/add")
    public Result<Object> add(@RequestBody Feedback feedback) {
        feedbackService.add(feedback);
        return Result.success("提交成功");
    }

    /**
     * 删除反馈
     */
    @DeleteMapping("/delete/{id}")
    public Result<Object> delete(@PathVariable Integer id) {
        feedbackService.deleteById(id);
        return Result.success("删除成功");
    }

    /**
     * 查询详情
     */
    @GetMapping("/{id}")
    public Result<Feedback> getById(@PathVariable Integer id) {
        return Result.success(feedbackService.getById(id));
    }

    /**
     * 管理员回复
     */
    @PostMapping("/reply")
    public Result<Object> reply(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        String reply = (String) params.get("reply");
        Integer adminId = (Integer) params.get("adminId");
        feedbackService.reply(id, reply, adminId);
        return Result.success("回复成功");
    }

    /**
     * 关闭反馈
     */
    @PutMapping("/close/{id}")
    public Result<Object> close(@PathVariable Integer id) {
        feedbackService.close(id);
        return Result.success("关闭成功");
    }
}
