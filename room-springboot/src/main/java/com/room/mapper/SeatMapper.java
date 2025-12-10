package com.room.mapper;

import com.room.entity.pojo.Seat;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SeatMapper {

    /**
     * 查询座位列表
     */
    List<Seat> list(@Param("roomId") Integer roomId, @Param("seatNumber") String seatNumber);

    /**
     * 根据ID查询
     */
    @Select("select * from seat where id = #{id}")
    Seat getById(Integer id);

    /**
     * 添加座位
     */
    @Insert("insert into seat (room_id, seat_number, seat_type, status) " +
            "VALUES (#{roomId}, #{seatNumber}, #{seatType}, #{status})")
    void add(Seat seat);

    /**
     * 批量添加座位
     */
    void batchAdd(@Param("seats") List<Seat> seats);

    /**
     * 更新座位
     */
    void update(Seat seat);

    /**
     * 删除座位
     */
    @Delete("delete from seat where id = #{id}")
    void delete(Integer id);

    /**
     * 根据自习室ID删除所有座位
     */
    @Delete("delete from seat where room_id = #{roomId}")
    void deleteByRoomId(Integer roomId);

    /**
     * 统计自习室座位数量
     */
    @Select("select count(*) from seat where room_id = #{roomId}")
    int countByRoomId(Integer roomId);

    /**
     * 查询可用座位（排除已预约的座位）
     */
    List<Seat> getAvailableSeats(@Param("roomId") Integer roomId, 
                                  @Param("startTime") String startTime, 
                                  @Param("endTime") String endTime,
                                  @Param("seatType") String seatType);
}
