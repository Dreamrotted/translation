package com.room.entity.pojo;

import lombok.Data;

/**
 * 自习室
 */
@Data
public class StudyRooms {

    private Integer id;

    /**
     * 自习室编号
     */
    private String roomNumber;

    /**
     * 可容纳人数
     */
    private Integer capacity;

    /**
     * 位置
     */
    private String location;

    /**
     * 已预约人数
     */
    private Integer reservedNumber;

    /**
     * 是否禁用（1是0否）
     */
    private Integer status;

    /**
     * 开放开始时间
     */
    private String openingTime;

    /**
     * 开放结束时间
     */
    private String closingTime;
}
