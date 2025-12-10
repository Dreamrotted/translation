package com.room.service;

import com.github.pagehelper.PageInfo;
import com.room.entity.pojo.Reservations;
import com.room.entity.pojo.Seat;

import java.util.List;

public interface ReservationService {

    PageInfo<Reservations> page(Integer pageNum, Integer pageSize, Integer studentNumber, String studentName, String roomNumber, String seatNumber, String status, String startDate, String endDate);

    void add(Reservations reservations);

    void update(Reservations reservations);

    /**
     * 查询可用座位
     */
    List<Seat> getAvailableSeats(Integer roomId, String startTime, String endTime, String seatType);

    /**
     * 取消预约
     */
    void cancelReservation(Integer reservationId, Integer studentId);

    /**
     * 签到
     */
    void checkIn(Integer reservationId, Integer studentId);

    /**
     * 签退
     */
    void checkOut(Integer reservationId, Integer studentId);
}
