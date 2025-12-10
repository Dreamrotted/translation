package com.room.entity.pojo;

import lombok.Data;

/**
 * 座位
 */
@Data
public class Seat {

    private Integer id;

    /**
     * 自习室ID
     */
    private Integer roomId;

    /**
     * 座位编号
     */
    private String seatNumber;

    /**
     * 座位类型（如：普通座位、插座座位、静音区等）
     */
    private String seatType;

    /**
     * 状态（0可用 1维修中 2禁用）
     */
    private Integer status;

    /**
     * 自习室信息（关联查询用）
     */
    private StudyRooms studyRooms;
}
