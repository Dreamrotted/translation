package com.room.service.impl;

import com.room.entity.pojo.StudyRooms;
import com.room.mapper.*;
import com.room.service.StatisticsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private StudyRoomsMapper studyRoomsMapper;

    @Resource
    private SeatMapper seatMapper;

    @Resource
    private ReservationMapper reservationMapper;

    @Resource
    private ViolationRecordMapper violationRecordMapper;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();

        // 获取所有自习室
        List<StudyRooms> rooms = studyRoomsMapper.list();
        
        // 计算总座位数
        int totalSeats = 0;
        for (StudyRooms room : rooms) {
            int seatCount = seatMapper.countByRoomId(room.getId());
            totalSeats += seatCount;
        }

        // 计算当前使用中的座位数（已签到未签退）
        int occupiedSeats = reservationMapper.countCurrentOccupied();

        // 计算总使用率
        double usageRate = totalSeats > 0 ? (double) occupiedSeats / totalSeats * 100 : 0;

        // 今日违规次数
        int todayViolations = violationRecordMapper.countTodayViolations();

        // 今日预约总数
        int todayReservations = reservationMapper.countTodayReservations();

        // 今日完成数
        int todayCompleted = reservationMapper.countTodayCompleted();

        result.put("totalSeats", totalSeats);
        result.put("occupiedSeats", occupiedSeats);
        result.put("usageRate", new DecimalFormat("#.##").format(usageRate));
        result.put("todayViolations", todayViolations);
        result.put("todayReservations", todayReservations);
        result.put("todayCompleted", todayCompleted);

        return result;
    }

    @Override
    public Map<String, Object> getRoomUsageComparison(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();

        // 如果没有指定日期，默认最近7天
        if (startDate == null || endDate == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            endDate = sdf.format(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, -6);
            startDate = sdf.format(cal.getTime());
        }

        // 获取所有自习室
        List<StudyRooms> rooms = studyRoomsMapper.list();
        List<String> roomNames = new ArrayList<>();
        List<Double> usageRates = new ArrayList<>();

        for (StudyRooms room : rooms) {
            roomNames.add(room.getRoomNumber());
            
            // 获取该自习室在时间段内的总座位数
            int seatCount = seatMapper.countByRoomId(room.getId());
            
            // 获取该自习室在时间段内的预约完成数
            int completedCount = reservationMapper.countCompletedByRoomAndDate(room.getId(), startDate, endDate);
            
            // 计算天数
            int days = calculateDays(startDate, endDate);
            
            // 计算使用率：完成数 / (座位数 * 天数) * 100
            double usageRate = (seatCount * days) > 0 ? (double) completedCount / (seatCount * days) * 100 : 0;
            usageRates.add(Double.parseDouble(new DecimalFormat("#.##").format(usageRate)));
        }

        result.put("roomNames", roomNames);
        result.put("usageRates", usageRates);
        result.put("startDate", startDate);
        result.put("endDate", endDate);

        return result;
    }

    @Override
    public Map<String, Object> getRoomHourlyTrend(Integer roomId, String date) {
        Map<String, Object> result = new HashMap<>();

        // 如果没有指定日期，默认今天
        if (date == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.format(new Date());
        }

        // 获取自习室信息
        List<StudyRooms> rooms = studyRoomsMapper.list();
        StudyRooms targetRoom = null;
        for (StudyRooms room : rooms) {
            if (room.getId().equals(roomId)) {
                targetRoom = room;
                break;
            }
        }

        if (targetRoom == null) {
            result.put("hours", new ArrayList<>());
            result.put("usageRates", new ArrayList<>());
            return result;
        }

        // 获取该自习室的座位数
        int seatCount = seatMapper.countByRoomId(roomId);

        // 构建24小时时间段
        List<String> hours = new ArrayList<>();
        List<Double> usageRates = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            hours.add(String.format("%02d:00", hour));
            
            // 统计该时段的历史平均使用率
            // 获取该时段已签到的预约数（基于历史数据的平均值）
            int occupiedCount = reservationMapper.countOccupiedByRoomAndHour(roomId, date, hour);
            
            double usageRate = seatCount > 0 ? (double) occupiedCount / seatCount * 100 : 0;
            usageRates.add(Double.parseDouble(new DecimalFormat("#.##").format(usageRate)));
        }

        result.put("hours", hours);
        result.put("usageRates", usageRates);
        result.put("roomName", targetRoom.getRoomNumber());
        result.put("date", date);

        return result;
    }

    @Override
    public Map<String, Object> getRealTimeOccupancy() {
        Map<String, Object> result = new HashMap<>();

        // 获取所有自习室
        List<StudyRooms> rooms = studyRoomsMapper.list();
        List<Map<String, Object>> roomData = new ArrayList<>();

        for (StudyRooms room : rooms) {
            Map<String, Object> data = new HashMap<>();
            
            // 获取该自习室的座位数
            int totalSeats = seatMapper.countByRoomId(room.getId());
            
            // 获取当前使用中的座位数
            int occupiedSeats = reservationMapper.countCurrentOccupiedByRoom(room.getId());
            
            // 计算使用率
            double usageRate = totalSeats > 0 ? (double) occupiedSeats / totalSeats * 100 : 0;
            
            data.put("roomId", room.getId());
            data.put("roomNumber", room.getRoomNumber());
            data.put("location", room.getLocation());
            data.put("totalSeats", totalSeats);
            data.put("occupiedSeats", occupiedSeats);
            data.put("availableSeats", totalSeats - occupiedSeats);
            data.put("usageRate", new DecimalFormat("#.##").format(usageRate));
            
            roomData.add(data);
        }

        result.put("rooms", roomData);
        result.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        return result;
    }

    /**
     * 计算两个日期之间的天数
     */
    private int calculateDays(String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            long diff = end.getTime() - start.getTime();
            return (int) (diff / (1000 * 60 * 60 * 24)) + 1;
        } catch (Exception e) {
            return 1;
        }
    }
}
