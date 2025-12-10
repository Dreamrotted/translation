package com.room.controller;

import com.github.pagehelper.PageInfo;
import com.room.common.Result;
import com.room.entity.pojo.Blacklist;
import com.room.service.BlacklistService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 黑名单管理
 */
@RestController
@RequestMapping("/blacklist")
public class BlacklistController {

    @Resource
    private BlacklistService blacklistService;

    /**
     * 分页查询黑名单
     */
    @GetMapping("/page")
    public Result<PageInfo<Blacklist>> page(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "studentNumber", required = false) Integer studentNumber,
            @RequestParam(value = "status", required = false) Integer status
    ) {
        return Result.success(blacklistService.page(pageNum, pageSize, studentNumber, status));
    }

    /**
     * 管理员手动添加黑名单
     */
    @PostMapping("/add")
    public Result<Object> add(@RequestBody Blacklist blacklist) {
        blacklistService.add(blacklist);
        return Result.success("添加成功");
    }

    /**
     * 更新黑名单
     */
    @PutMapping("/update")
    public Result<Object> update(@RequestBody Blacklist blacklist) {
        blacklistService.update(blacklist);
        return Result.success("更新成功");
    }

    /**
     * 删除黑名单
     */
    @DeleteMapping("/delete/{id}")
    public Result<Object> delete(@PathVariable Integer id) {
        blacklistService.deleteById(id);
        return Result.success("删除成功");
    }

    /**
     * 解除黑名单
     */
    @PutMapping("/remove/{id}")
    public Result<Object> remove(@PathVariable Integer id) {
        blacklistService.removeFromBlacklist(id);
        return Result.success("解除成功");
    }
}
