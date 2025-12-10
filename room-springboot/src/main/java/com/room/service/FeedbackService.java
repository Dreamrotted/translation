package com.room.service;

import com.github.pagehelper.PageInfo;
import com.room.entity.pojo.Feedback;

public interface FeedbackService {

    PageInfo<Feedback> page(Integer pageNum, Integer pageSize, Integer studentNumber, Integer type, Integer status, String startDate, String endDate);

    void add(Feedback feedback);

    void update(Feedback feedback);

    void deleteById(Integer id);

    Feedback getById(Integer id);

    /**
     * 管理员回复
     */
    void reply(Integer id, String reply, Integer adminId);

    /**
     * 关闭反馈
     */
    void close(Integer id);
}
