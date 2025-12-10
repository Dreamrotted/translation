package com.room.service;

import com.github.pagehelper.PageInfo;
import com.room.entity.pojo.ViolationRecord;

public interface ViolationRecordService {

    PageInfo<ViolationRecord> page(Integer pageNum, Integer pageSize, Integer studentNumber, String violationType, Integer recordType, Integer status, String startDate, String endDate);

    void add(ViolationRecord violationRecord);

    void update(ViolationRecord violationRecord);

    void deleteById(Integer id);

    /**
     * 记录违规（系统自动）
     */
    void recordViolation(Integer studentId, Integer reservationId, String violationType, String violationDesc);

    /**
     * 检查并自动加入黑名单
     */
    void checkAndAddToBlacklist(Integer studentId);

    /**
     * 获取学生本月违规次数
     */
    int getMonthViolationCount(Integer studentId);
}
