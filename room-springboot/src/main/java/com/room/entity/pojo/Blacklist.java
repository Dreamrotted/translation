package com.room.entity.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 黑名单
 */
@Data
public class Blacklist {

    private Integer id;

    /**
     * 学生ID
     */
    private Integer studentId;

    /**
     * 加入黑名单原因
     */
    private String reason;

    /**
     * 黑名单开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 黑名单结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 加入方式（0系统自动 1管理员手动）
     */
    private Integer addType;

    /**
     * 操作管理员ID（手动加入时）
     */
    private Integer adminId;

    /**
     * 状态（0已解除 1生效中）
     */
    private Integer status;

    /**
     * 学生信息
     */
    private Student student;
}
