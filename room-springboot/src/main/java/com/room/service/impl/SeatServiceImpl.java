package com.room.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.room.entity.pojo.Seat;
import com.room.entity.pojo.StudyRooms;
import com.room.exception.BusinessException;
import com.room.mapper.SeatMapper;
import com.room.mapper.StudyRoomsMapper;
import com.room.service.SeatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeatServiceImpl implements SeatService {

    @Resource
    private SeatMapper seatMapper;

    @Resource
    private StudyRoomsMapper studyRoomsMapper;

    @Override
    public PageInfo<Seat> page(Integer pageNum, Integer pageSize, Integer roomId, String seatNumber) {
        PageHelper.startPage(pageNum, pageSize);
        List<Seat> list = seatMapper.list(roomId, seatNumber);
        return new PageInfo<>(list);
    }

    @Override
    public void add(Seat seat) {
        // 验证教室容量
        validateRoomCapacity(seat.getRoomId(), 1);
        
        if (seat.getStatus() == null) {
            seat.setStatus(0); // 默认可用
        }
        seatMapper.add(seat);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchGenerate(Integer roomId, Integer count, String prefix, String seatType) {
        if (roomId == null || count == null || count <= 0) {
            throw new BusinessException("参数错误");
        }
        if (count > 200) {
            throw new BusinessException("批量生成座位数量不能超过200个");
        }

        // 验证教室容量
        validateRoomCapacity(roomId, count);

        // 获取当前教室已有座位数,从现有数量+1开始编号
        int currentSeatCount = seatMapper.countByRoomId(roomId);
        int startNumber = currentSeatCount + 1;

        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Seat seat = new Seat();
            seat.setRoomId(roomId);
            // 从现有座位数+1开始编号,如:已有5个座位,则从006开始
            seat.setSeatNumber((prefix != null ? prefix : "") + String.format("%03d", startNumber + i));
            seat.setSeatType(seatType != null ? seatType : "普通座位");
            seat.setStatus(0); // 默认可用
            seats.add(seat);
        }

        seatMapper.batchAdd(seats);
    }

    @Override
    public void update(Seat seat) {
        seatMapper.update(seat);
    }

    @Override
    public void delete(Integer id) {
        seatMapper.delete(id);
    }

    @Override
    public List<Seat> listAll() {
        return seatMapper.list(null, null);
    }

    /**
     * 验证教室容量
     */
    private void validateRoomCapacity(Integer roomId, Integer addCount) {
        // 根据ID直接查询教室信息
        StudyRooms room = studyRoomsMapper.getById(roomId);
        
        if (room == null) {
            throw new BusinessException("教室不存在");
        }
        
        // 获取当前教室已有座位数
        int currentSeatCount = seatMapper.countByRoomId(roomId);
        
        // 计算添加后的总座位数
        int totalSeatCount = currentSeatCount + addCount;
        
        // 验证是否超过教室容量
        if (totalSeatCount > room.getCapacity()) {
            throw new BusinessException(
                String.format("添加失败！教室最大容纳人数为%d人，当前已有%d个座位，无法再添加%d个座位", 
                    room.getCapacity(), currentSeatCount, addCount)
            );
        }
    }
}
