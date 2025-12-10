package com.room.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.room.entity.pojo.Feedback;
import com.room.exception.BusinessException;
import com.room.mapper.FeedbackMapper;
import com.room.service.FeedbackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Resource
    private FeedbackMapper feedbackMapper;

    @Override
    public PageInfo<Feedback> page(Integer pageNum, Integer pageSize, Integer studentNumber, Integer type, Integer status, String startDate, String endDate) {
        PageHelper.startPage(pageNum, pageSize);
        List<Feedback> list = feedbackMapper.list(studentNumber, type, status, startDate, endDate);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Feedback feedback) {
        feedback.setStatus(0); // 待处理
        feedbackMapper.add(feedback);
    }

    @Override
    public void update(Feedback feedback) {
        feedbackMapper.update(feedback);
    }

    @Override
    public void deleteById(Integer id) {
        feedbackMapper.deleteById(id);
    }

    @Override
    public Feedback getById(Integer id) {
        return feedbackMapper.getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reply(Integer id, String reply, Integer adminId) {
        Feedback feedback = feedbackMapper.getById(id);
        if (feedback == null) {
            throw new BusinessException("反馈不存在");
        }
        
        feedback.setReply(reply);
        feedback.setReplyAdminId(adminId);
        feedback.setReplyTime(new Date());
        feedback.setStatus(1); // 已回复
        feedbackMapper.update(feedback);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void close(Integer id) {
        Feedback feedback = feedbackMapper.getById(id);
        if (feedback == null) {
            throw new BusinessException("反馈不存在");
        }
        
        feedback.setStatus(2); // 已关闭
        feedbackMapper.update(feedback);
    }
}
