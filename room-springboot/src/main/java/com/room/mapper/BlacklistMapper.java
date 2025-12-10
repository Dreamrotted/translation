package com.room.mapper;

import com.room.entity.pojo.Blacklist;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

public interface BlacklistMapper {

    List<Blacklist> list(
            @Param("studentNumber") Integer studentNumber,
            @Param("status") Integer status
    );

    void add(Blacklist blacklist);

    void update(Blacklist blacklist);

    void deleteById(Integer id);

    @Select("select * from blacklist where id = #{id}")
    Blacklist getById(Integer id);

    /**
     * 查询学生是否在黑名单中（生效中）
     */
    @Select("select * from blacklist where student_id = #{studentId} and status = 1 and end_time > NOW() order by id desc limit 1")
    Blacklist getActiveBlacklist(Integer studentId);

    /**
     * 查询所有过期的黑名单记录
     */
    @Select("select * from blacklist where status = 1 and end_time <= #{now}")
    List<Blacklist> getExpiredBlacklists(Date now);
}
