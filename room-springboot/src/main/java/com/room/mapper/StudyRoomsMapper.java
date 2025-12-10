package com.room.mapper;

import com.room.entity.pojo.StudyRooms;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

public interface StudyRoomsMapper {

    @Select("select * from study_rooms")
    List<StudyRooms> list();

    @Delete("delete from study_rooms where id = #{id}")
    void delete(Integer id);

    @Insert("insert into study_rooms (room_number, capacity, location, status, opening_time, closing_time) " +
            "VALUE (#{roomNumber}, #{capacity}, #{location}, #{status}, #{openingTime}, #{closingTime})" 
    )
    void add(StudyRooms studyRooms);

    void update(StudyRooms studyRooms);

    Integer getResidualCapacity(int roomId, Date startTime, Date endTime);

    @Select("select * from study_rooms where id = #{roomId}")
    StudyRooms getById(int roomId);
}
