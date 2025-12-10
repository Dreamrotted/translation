package com.room.entity.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 违规记录
 */
@Data
public class ViolationRecord {

    private Integer id;

    /**
     * 学生ID
     */
    private Integer studentId;

    /**
     * 预约记录ID（系统自动记录时关联）
     */
    private Integer reservationId;

    /**
     * 违规类型（未按时签到、未主动签退、其他）
     */
    private String violationType;

    /**
     * 违规描述
     */
    private String violationDesc;

    /**
     * 违规时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date violationTime;

    /**
     * 记录类型（0系统自动 1管理员手动）
     */
    private Integer recordType;

    /**
     * 记录管理员ID（手动记录时）
     */
    private Integer adminId;

    /**
     * 处理状态（0未处理 1已处理）
     */
    private Integer status;

    /**
     * 学生信息
     */
    private Student student;

    /**
     * 预约信息
     */
    private Reservations reservation;
}
