package com.room.entity.emums;

import lombok.Getter;

/**
 * 预约状态
 */
@Getter
public enum ReservationStatus {

    RESERVED("已预约"),

    CANCELED("已取消"),

    EXPIRED("已过期"),

    COMPLETED("已完成");

    private final String status;

    ReservationStatus(String status) {
        this.status = status;
    }
}
