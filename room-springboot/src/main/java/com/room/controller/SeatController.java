package com.room.controller;

import com.github.pagehelper.PageInfo;
import com.room.common.Result;
import com.room.entity.pojo.Seat;
import com.room.service.SeatService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 座位管理
 */
@RestController
@RequestMapping("/seat")
public class SeatController {

    @Resource
    private SeatService seatService;

    /**
     * 分页查询座位
     */
    @GetMapping("/page")
    public Result<PageInfo<Seat>> page(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "roomId", required = false) Integer roomId,
            @RequestParam(value = "seatNumber", required = false) String seatNumber
    ) {
        PageInfo<Seat> pageInfo = seatService.page(pageNum, pageSize, roomId, seatNumber);
        return Result.success(pageInfo);
    }

    /**
     * 添加座位
     */
    @PostMapping("/add")
    public Result<Object> add(@RequestBody Seat seat) {
        seatService.add(seat);
        return Result.success("添加成功");
    }

    /**
     * 批量生成座位
     */
    @PostMapping("/batchGenerate")
    public Result<Object> batchGenerate(@RequestBody Map<String, Object> params) {
        Integer roomId = (Integer) params.get("roomId");
        Integer count = (Integer) params.get("count");
        String prefix = (String) params.get("prefix");
        String seatType = (String) params.get("seatType");
        
        seatService.batchGenerate(roomId, count, prefix, seatType);
        return Result.success("批量生成成功");
    }

    /**
     * 更新座位
     */
    @PutMapping("/update")
    public Result<Object> update(@RequestBody Seat seat) {
        seatService.update(seat);
        return Result.success("更新成功");
    }

    /**
     * 删除座位
     */
    @DeleteMapping("/delete/{id}")
    public Result<Object> delete(@PathVariable Integer id) {
        seatService.delete(id);
        return Result.success("删除成功");
    }
}
