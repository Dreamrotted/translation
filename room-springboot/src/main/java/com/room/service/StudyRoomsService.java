package com.room.service;

import com.github.pagehelper.PageInfo;
import com.room.entity.pojo.StudyRooms;

import java.util.List;

public interface StudyRoomsService {

    PageInfo<StudyRooms> page(Integer pageNum, Integer pageSize);

    void delete(Integer id);

    void update(StudyRooms studyRooms);

    void add(StudyRooms studyRooms);

    /**
     * 获取所有自习室列表
     */
    List<StudyRooms> list();
}
