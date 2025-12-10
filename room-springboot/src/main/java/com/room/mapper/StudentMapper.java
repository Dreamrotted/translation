package com.room.mapper;

import com.room.entity.pojo.Student;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface StudentMapper {

    @Select("select * from student where student_number =  #{studentNumber}")
    Student getByStudentNumber(Integer studentNumber);

    @Select("select * from student")
    List<Student> list();

    @Insert("insert into student (student_number, student_name, gender, password, major_name) " +
            "VALUE (#{studentNumber},#{studentName},#{gender},#{password},#{majorName})"
    )
    void add(Student student);

    @Delete("delete from student where id = #{id}")
    void deleteById(Integer id);

    void update(Student student);

    @Select("select * from student where id = #{id}")
    Student getById(Integer id);
}
