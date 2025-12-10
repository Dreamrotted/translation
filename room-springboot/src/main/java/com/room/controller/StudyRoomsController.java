package com.room.controller;

import com.github.pagehelper.PageInfo;
import com.room.common.Result;
import com.room.entity.pojo.StudyRooms;
import com.room.service.StudyRoomsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 教室管理
 */
@RestController
@RequestMapping("/studyRooms")
public class StudyRoomsController {

    @Resource
    private StudyRoomsService StudyRoomsService;

    /**
     * 查询所有教室
     *
     * @return Result
     */
    @GetMapping("/page")
    public Result<PageInfo<StudyRooms>> page(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageInfo<StudyRooms> pageInfo = StudyRoomsService.page(pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 添加
     * @param studyRooms 自习室信息
     * @return Result
     */
    @PostMapping("/add")
    public Result<Object> add(@RequestBody StudyRooms studyRooms) {
        StudyRoomsService.add(studyRooms);
        return Result.success("添加成功");
    }

    /**
     * 修改
     * @param studyRooms 自习室信息
     * @return Result
     */
    @PutMapping("/update")
    public Result<Object> update(@RequestBody StudyRooms studyRooms) {
        StudyRoomsService.update(studyRooms);
        return Result.success();
    }

    /**
     * 删除
     * @param id id
     * @return Result
     */
    @DeleteMapping("/delete/{id}")
    public Result<Object> delete(@PathVariable Integer id) {
        StudyRoomsService.delete(id);
        return Result.success();
    }

    /**
     * 获取所有自习室列表（不分页）
     * @return Result
     */
    @GetMapping("/list")
    public Result<List<StudyRooms>> list() {
        List<StudyRooms> list = StudyRoomsService.list();
        return Result.success(list);
    }
}
