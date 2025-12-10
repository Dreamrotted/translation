package com.room.mapper;

import com.room.entity.pojo.ViolationRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

public interface ViolationRecordMapper {

    List<ViolationRecord> list(
            @Param("studentNumber") Integer studentNumber,
            @Param("violationType") String violationType,
            @Param("recordType") Integer recordType,
            @Param("status") Integer status,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    void add(ViolationRecord violationRecord);

    void update(ViolationRecord violationRecord);

    void deleteById(Integer id);

    @Select("select * from violation_record where id = #{id}")
    ViolationRecord getById(Integer id);

    /**
     * 统计学生本月违规次数
     */
    @Select("select count(*) from violation_record where student_id = #{studentId} " +
            "and DATE_FORMAT(violation_time, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')")
    int countMonthViolations(Integer studentId);

    /**
     * 查询学生的违规记录
     */
    @Select("select * from violation_record where student_id = #{studentId} order by violation_time desc")
    List<ViolationRecord> getByStudentId(Integer studentId);

    /**
     * 统计今日违规次数
     */
    @Select("select count(*) from violation_record where date(violation_time) = date(now())")
    int countTodayViolations();
}
