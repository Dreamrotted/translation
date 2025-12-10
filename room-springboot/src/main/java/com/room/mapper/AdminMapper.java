package com.room.mapper;

import com.room.entity.pojo.Admin;
import org.apache.ibatis.annotations.Select;

public interface AdminMapper {


    @Select("select * from admin where account = #{account}")
    Admin getByAccount(String account);
}
