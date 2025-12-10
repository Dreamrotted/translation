package com.room.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.room.entity.emums.ReservationStatus;
import com.room.entity.pojo.Reservations;
import com.room.entity.pojo.Seat;
import com.room.entity.pojo.StudyRooms;
import com.room.entity.pojo.ViolationRecord;
import com.room.exception.BusinessException;
import com.room.mapper.ReservationMapper;
import com.room.mapper.SeatMapper;
import com.room.mapper.StudyRoomsMapper;
import com.room.mapper.ViolationRecordMapper;
import com.room.mapper.BlacklistMapper;
import com.room.service.ReservationService;
import com.room.service.ViolationRecordService;
import com.room.service.BlacklistService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Service
public class ReservationServiceImpl implements ReservationService {


    @Resource
    private ReservationMapper reservationMapper;

    @Resource
    private StudyRoomsMapper studyRoomsMapper;

    @Resource
    private SeatMapper seatMapper;

    @Resource
    private ViolationRecordMapper violationRecordMapper;

    @Resource
    private ViolationRecordService violationRecordService;

    @Resource
    private BlacklistMapper blacklistMapper;

    @Resource
    private BlacklistService blacklistService;

    @Override
    public PageInfo<Reservations> page(Integer pageNum, Integer pageSize, Integer studentNumber, String studentName, String roomNumber, String seatNumber, String status, String startDate, String endDate) {
        PageHelper.startPage(pageNum, pageSize);
        List<Reservations> list = reservationMapper.list(studentNumber, studentName, roomNumber, seatNumber, status, startDate, endDate);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Reservations reservations) {
        // 检查是否在黑名单中
        if (blacklistService.isInBlacklist(reservations.getStudentId())) {
            throw new BusinessException("您已被加入黑名单，暂时无法预约");
        }
        
        // 验证座位是否存在
        if (reservations.getSeatId() == null) {
            throw new BusinessException("请选择座位");
        }
        
        Seat seat = seatMapper.getById(reservations.getSeatId());
        if (seat == null) {
            throw new BusinessException("座位不存在");
        }
        
        if (seat.getStatus() != 0) {
            throw new BusinessException("该座位不可用");
        }
        
        // 设置roomId
        reservations.setRoomId(seat.getRoomId());
        
        // 查询用户今天是否有预约
        Reservations dbReservations = reservationMapper.getByStudentId(reservations);
        if (dbReservations != null) {
            throw new BusinessException("今天已有预约");
        }
        
        reservations.setStatus(ReservationStatus.RESERVED.getStatus());
        reservationMapper.add(reservations);
    }

    @Override
    public void update(Reservations reservations) {
        reservationMapper.update(reservations);
    }

    @Override
    public List<Seat> getAvailableSeats(Integer roomId, String startTime, String endTime, String seatType) {
        return seatMapper.getAvailableSeats(roomId, startTime, endTime, seatType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelReservation(Integer reservationId, Integer studentId) {
        Reservations reservation = reservationMapper.list(null, null, null, null, null, null, null).stream()
                .filter(r -> r.getId().equals(reservationId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("预约不存在"));
        
        // 验证是否为当前学生的预约
        if (!reservation.getStudentId().equals(studentId)) {
            throw new BusinessException("无权取消此预约");
        }
        
        // 验证预约状态
        if (!"已预约".equals(reservation.getStatus())) {
            throw new BusinessException("只能取消已预约状态的记录");
        }
        
        // 验证是否在开始时间前30分钟
        Date now = new Date();
        long diffMinutes = (reservation.getStartTime().getTime() - now.getTime()) / (1000 * 60);
        if (diffMinutes < 30) {
            throw new BusinessException("预约开始前30分钟内不能取消");
        }
        
        reservation.setStatus("已取消");
        reservationMapper.update(reservation);
    }

    /**
     * 定时任务：每5分钟执行一次，查询预约的结束时间大于当前时间后修改为已过期
     */
    @Scheduled(fixedRate = 300000) // 5分钟执行一次
    public void updateExpiredReservations() {
        // 获取当前时间
        Date now = new Date();

        // 查询结束时间大于当前时间的记录
        List<Reservations> reservations = reservationMapper.getReservationsEndingAfter(now);

        // 更新这些记录的状态为“已过期”
        for (Reservations reservation : reservations) {
            reservation.setStatus(ReservationStatus.EXPIRED.getStatus());
            reservationMapper.update(reservation);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkIn(Integer reservationId, Integer studentId) {
        // 获取预约记录
        Reservations reservation = reservationMapper.list(null, null, null, null, null, null, null).stream()
                .filter(r -> r.getId().equals(reservationId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("预约不存在"));
        
        // 验证是否为当前学生的预约
        if (!reservation.getStudentId().equals(studentId)) {
            throw new BusinessException("无权签到此预约");
        }
        
        // 验证预约状态
        if (!"已预约".equals(reservation.getStatus())) {
            throw new BusinessException("只能签到已预约状态的记录");
        }
        
        // 验证是否已签到
        if (reservation.getIsCheckedIn() != null && reservation.getIsCheckedIn() == 1) {
            throw new BusinessException("已经签到，无需重复签到");
        }
        
        Date now = new Date();
        
        // 验证是否在预约开始时间之后
        if (now.before(reservation.getStartTime())) {
            throw new BusinessException("预约还未开始，无法签到");
        }
        
        // 验证是否在开始后15分钟内
        long diffMinutes = (now.getTime() - reservation.getStartTime().getTime()) / (1000 * 60);
        if (diffMinutes > 15) {
            // 超过15分钟未签到，自动取消预约
            reservation.setStatus("已取消");
            reservationMapper.update(reservation);
            
            // 记录违规：未按时签到
            violationRecordService.recordViolation(studentId, reservation.getId(), "未按时签到", 
                "预约开始后超过15分钟未签到，预约已自动取消");
            
            throw new BusinessException("签到超时，预约已自动取消");
        }
        
        // 签到
        reservation.setCheckInTime(now);
        reservation.setIsCheckedIn(1);
        reservationMapper.update(reservation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkOut(Integer reservationId, Integer studentId) {
        // 获取预约记录
        Reservations reservation = reservationMapper.list(null, null, null, null, null, null, null).stream()
                .filter(r -> r.getId().equals(reservationId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("预约不存在"));
        
        // 验证是否为当前学生的预约
        if (!reservation.getStudentId().equals(studentId)) {
            throw new BusinessException("无权签退此预约");
        }
        
        // 验证是否已签到
        if (reservation.getIsCheckedIn() == null || reservation.getIsCheckedIn() == 0) {
            throw new BusinessException("请先签到后再签退");
        }
        
        // 验证是否已签退
        if (reservation.getIsCheckedOut() != null && reservation.getIsCheckedOut() == 1) {
            throw new BusinessException("已经签退，无需重复签退");
        }
        
        Date now = new Date();
        
        // 签退
        reservation.setCheckOutTime(now);
        reservation.setIsCheckedOut(1);
        reservation.setStatus("已完成");
        reservationMapper.update(reservation);
    }

    /**
     * 定时任务：每晚22:00自动签退所有未签退的预约
     */
    @Scheduled(cron = "0 0 22 * * ?") // 每晚22:00执行
    public void autoCheckOut() {
        // 查询所有已签到但未签退的预约
        List<Reservations> reservations = reservationMapper.list(null, null, null, null, null, null, null);
        Date now = new Date();
        
        for (Reservations reservation : reservations) {
            // 已签到但未签退，且状态为已预约
            if (reservation.getIsCheckedIn() != null && reservation.getIsCheckedIn() == 1
                    && (reservation.getIsCheckedOut() == null || reservation.getIsCheckedOut() == 0)
                    && "已预约".equals(reservation.getStatus())) {
                reservation.setCheckOutTime(now);
                reservation.setIsCheckedOut(1);
                reservation.setStatus("已完成");
                reservationMapper.update(reservation);
                
                // 记录违规：未主动签退
                violationRecordService.recordViolation(reservation.getStudentId(), reservation.getId(), 
                    "未主动签退", "系统晚上22:00自动签退");
            }
        }
    }
}
