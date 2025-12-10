package com.room.controller;

import com.room.common.Result;
import com.room.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 数据统计分析
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    /**
     * 获取实时概览数据
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        return Result.success(statisticsService.getOverview());
    }

    /**
     * 获取各自习室使用率对比（柱状图）
     */
    @GetMapping("/roomUsageComparison")
    public Result<Map<String, Object>> getRoomUsageComparison(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ) {
        return Result.success(statisticsService.getRoomUsageComparison(startDate, endDate));
    }

    /**
     * 获取自习室时段使用率趋势（折线图）
     */
    @GetMapping("/roomHourlyTrend")
    public Result<Map<String, Object>> getRoomHourlyTrend(
            @RequestParam(value = "roomId") Integer roomId,
            @RequestParam(value = "date", required = false) String date
    ) {
        return Result.success(statisticsService.getRoomHourlyTrend(roomId, date));
    }

    /**
     * 获取各自习室实时人数
     */
    @GetMapping("/realTimeOccupancy")
    public Result<Map<String, Object>> getRealTimeOccupancy() {
        return Result.success(statisticsService.getRealTimeOccupancy());
    }
}
