package com.room.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.room.entity.pojo.StudyRooms;
import com.room.mapper.ReservationMapper;
import com.room.mapper.StudyRoomsMapper;
import com.room.service.StudyRoomsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class StudyRoomsServiceImpl implements StudyRoomsService {

    @Resource
    private StudyRoomsMapper studyRoomsMapper;

    @Resource
    private ReservationMapper reservationMapper;

    @Override
    public PageInfo<StudyRooms> page(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<StudyRooms> list = studyRoomsMapper.list();
        for (StudyRooms studyRooms : list) {
            int reservedNumberByRoomId = reservationMapper.getReservedNumberByRoomId(studyRooms.getId());
            studyRooms.setReservedNumber(reservedNumberByRoomId);
        }
        return new PageInfo<>(list);
    }

    @Override
    public void delete(Integer id) {
        studyRoomsMapper.delete(id);
    }

    @Override
    public void update(StudyRooms studyRooms) {
        studyRoomsMapper.update(studyRooms);
    }

    @Override
    public void add(StudyRooms studyRooms) {
        studyRoomsMapper.add(studyRooms);
    }

    @Override
    public List<StudyRooms> list() {
        return studyRoomsMapper.list();
    }
}
