package com.room.entity.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 投诉与评价
 */
@Data
public class Feedback {

    private Integer id;

    /**
     * 学生ID
     */
    private Integer studentId;

    /**
     * 类型（0投诉 1评价）
     */
    private Integer type;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 关联自习室ID
     */
    private Integer roomId;

    /**
     * 关联座位ID
     */
    private Integer seatId;

    /**
     * 状态（0待处理 1已回复 2已关闭）
     */
    private Integer status;

    /**
     * 管理员回复
     */
    private String reply;

    /**
     * 回复管理员ID
     */
    private Integer replyAdminId;

    /**
     * 回复时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date replyTime;

    /**
     * 提交时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 学生信息
     */
    private Student student;

    /**
     * 自习室信息
     */
    private StudyRooms studyRooms;

    /**
     * 座位信息
     */
    private Seat seat;
}
