package com.room.controller;

import com.github.pagehelper.PageInfo;
import com.room.common.Result;
import com.room.entity.pojo.Reservations;
import com.room.entity.pojo.Seat;
import com.room.service.ReservationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 预约记录管理
 */
@RestController
@RequestMapping("/reservation")
public class ReservationController {


    @Resource
    private ReservationService reservationService;

    /**
     * 分页查询预约记录
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param studentNumber 学号
     * @param studentName 姓名
     * @param roomNumber 自习室编号
     * @param seatNumber 座位编号
     * @param status 预约状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return Result
     */
    @GetMapping("/page")
    public Result<PageInfo<Reservations>> page(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "studentNumber", required = false) Integer studentNumber,
            @RequestParam(value = "studentName", required = false) String studentName,
            @RequestParam(value = "roomNumber", required = false) String roomNumber,
            @RequestParam(value = "seatNumber", required = false) String seatNumber,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ) {
        return Result.success(reservationService.page(pageNum, pageSize, studentNumber, studentName, roomNumber, seatNumber, status, startDate, endDate));
    }

    /**
     * 学生预约
     *
     * @param reservations 预约记录
     * @return Result
     */
    @PostMapping("/add")
    public Result<Object> add(@RequestBody Reservations reservations) {
        reservationService.add(reservations);
        return Result.success();
    }

    /**
     * 修改预约记录
     *
     * @param reservations 预约记录
     * @return Result
     */
    @PutMapping("/update")
    public Result<Object> update(@RequestBody Reservations reservations) {
        reservationService.update(reservations);
        return Result.success();
    }

    /**
     * 查询可用座位
     */
    @GetMapping("/availableSeats")
    public Result<List<Seat>> getAvailableSeats(
            @RequestParam("roomId") Integer roomId,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime,
            @RequestParam(value = "seatType", required = false) String seatType
    ) {
        List<Seat> seats = reservationService.getAvailableSeats(roomId, startTime, endTime, seatType);
        return Result.success(seats);
    }

    /**
     * 取消预约
     */
    @PostMapping("/cancel")
    public Result<Object> cancel(@RequestBody Map<String, Integer> params) {
        Integer reservationId = params.get("reservationId");
        Integer studentId = params.get("studentId");
        reservationService.cancelReservation(reservationId, studentId);
        return Result.success("取消成功");
    }

    /**
     * 签到
     */
    @PostMapping("/checkIn")
    public Result<Object> checkIn(@RequestBody Map<String, Integer> params) {
        Integer reservationId = params.get("reservationId");
        Integer studentId = params.get("studentId");
        reservationService.checkIn(reservationId, studentId);
        return Result.success("签到成功");
    }

    /**
     * 签退
     */
    @PostMapping("/checkOut")
    public Result<Object> checkOut(@RequestBody Map<String, Integer> params) {
        Integer reservationId = params.get("reservationId");
        Integer studentId = params.get("studentId");
        reservationService.checkOut(reservationId, studentId);
        return Result.success("签退成功");
    }
}
