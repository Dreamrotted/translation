package com.room.service;

import com.github.pagehelper.PageInfo;
import com.room.entity.pojo.Seat;

import java.util.List;

public interface SeatService {

    /**
     * 分页查询座位
     */
    PageInfo<Seat> page(Integer pageNum, Integer pageSize, Integer roomId, String seatNumber);

    /**
     * 添加座位
     */
    void add(Seat seat);

    /**
     * 批量生成座位
     */
    void batchGenerate(Integer roomId, Integer count, String prefix, String seatType);

    /**
     * 更新座位
     */
    void update(Seat seat);

    /**
     * 删除座位
     */
    void delete(Integer id);

    /**
     * 获取所有自习室列表（用于下拉选择）
     */
    List<Seat> listAll();
}
