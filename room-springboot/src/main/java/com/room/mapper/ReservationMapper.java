package com.room.mapper;

import com.room.entity.pojo.Reservations;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

public interface ReservationMapper {


    List<Reservations> list(
            @Param("studentNumber") Integer studentNumber,
            @Param("studentName") String studentName,
            @Param("roomNumber") String roomNumber,
            @Param("seatNumber") String seatNumber,
            @Param("status") String status,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    void add(Reservations reservations);

    // 查询学生今日是否有预约
    @Select("select * from reservations where student_id = #{studentId} and date(start_time) = date(#{startTime}) and room_id = #{roomId} ")
    Reservations getByStudentId(Reservations reservations);

    void update(Reservations reservations);

    @Select("select count(id) from reservations where room_id = #{roomId} and status = '已预约'")
    int getReservedNumberByRoomId(Integer roomId);

    // 查询结束时间大于当前时间的记录
    @Select("select * from reservations where end_time < #{endTime}")
    List<Reservations> getReservationsEndingAfter(Date endTime);

    /**
     * 统计当前使用中的座位数（已签到未签退）
     */
    @Select("select count(*) from reservations where is_checked_in = 1 and is_checked_out = 0 and date(start_time) = date(now())")
    int countCurrentOccupied();

    /**
     * 统计今日预约总数
     */
    @Select("select count(*) from reservations where date(start_time) = date(now())")
    int countTodayReservations();

    /**
     * 统计今日完成数
     */
    @Select("select count(*) from reservations where date(start_time) = date(now()) and status = '已完成'")
    int countTodayCompleted();

    /**
     * 统计自习室在时间段内的完成数
     */
    @Select("select count(*) from reservations where room_id = #{roomId} and date(start_time) >= #{startDate} and date(start_time) <= #{endDate} and status = '已完成'")
    int countCompletedByRoomAndDate(@Param("roomId") Integer roomId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 统计自习室某时段的使用人数
     */
    @Select("select count(*) from reservations where room_id = #{roomId} and date(start_time) = #{date} and hour(start_time) = #{hour} and is_checked_in = 1")
    int countOccupiedByRoomAndHour(@Param("roomId") Integer roomId, @Param("date") String date, @Param("hour") int hour);

    /**
     * 统计自习室当前使用中的座位数
     */
    @Select("select count(*) from reservations where room_id = #{roomId} and is_checked_in = 1 and is_checked_out = 0 and date(start_time) = date(now())")
    int countCurrentOccupiedByRoom(Integer roomId);
}
