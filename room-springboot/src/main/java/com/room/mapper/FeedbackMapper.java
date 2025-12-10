package com.room.mapper;

import com.room.entity.pojo.Feedback;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FeedbackMapper {

    List<Feedback> list(
            @Param("studentNumber") Integer studentNumber,
            @Param("type") Integer type,
            @Param("status") Integer status,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    void add(Feedback feedback);

    void update(Feedback feedback);

    void deleteById(Integer id);

    Feedback getById(Integer id);

    /**
     * 查询学生的反馈列表
     */
    List<Feedback> getByStudentId(Integer studentId);
}
