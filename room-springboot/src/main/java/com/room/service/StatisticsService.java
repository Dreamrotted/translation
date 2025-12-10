package com.room.service;

import java.util.Map;

public interface StatisticsService {

    /**
     * 获取实时概览数据
     */
    Map<String, Object> getOverview();

    /**
     * 获取各自习室使用率对比
     */
    Map<String, Object> getRoomUsageComparison(String startDate, String endDate);

    /**
     * 获取自习室时段使用率趋势
     */
    Map<String, Object> getRoomHourlyTrend(Integer roomId, String date);

    /**
     * 获取各自习室实时人数
     */
    Map<String, Object> getRealTimeOccupancy();
}
