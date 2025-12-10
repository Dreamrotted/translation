package com.room.entity.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 预约记录
 * 预约记录表：学号、教室号、预约时间段、是否签到、是否违规。
 */
@Data
public class Reservations {

    private Integer id;
    /**
     * 学生Id
     */
    private Integer studentId;

    /**
     * 自习室Id
     */
    private Integer roomId;

    /**
     * 座位Id
     */
    private Integer seatId;

    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;


    /**
     * 状态
     */
    private String status;

    /**
     * 签到时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date checkInTime;

    /**
     * 签退时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date checkOutTime;

    /**
     * 是否已签到（0未签到 1已签到）
     */
    private Integer isCheckedIn;

    /**
     * 是否已签退（0未签退 1已签退）
     */
    private Integer isCheckedOut;

    private Student student;

    private StudyRooms studyRooms;

    private Seat seat;
}