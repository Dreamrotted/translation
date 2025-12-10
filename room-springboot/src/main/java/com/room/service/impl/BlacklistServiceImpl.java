package com.room.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.room.entity.pojo.Blacklist;
import com.room.mapper.BlacklistMapper;
import com.room.service.BlacklistService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class BlacklistServiceImpl implements BlacklistService {

    @Resource
    private BlacklistMapper blacklistMapper;

    @Override
    public PageInfo<Blacklist> page(Integer pageNum, Integer pageSize, Integer studentNumber, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        List<Blacklist> list = blacklistMapper.list(studentNumber, status);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Blacklist blacklist) {
        blacklist.setStartTime(new Date());
        
        // 如果没有指定结束时间，默认7天后
        if (blacklist.getEndTime() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            blacklist.setEndTime(calendar.getTime());
        }
        
        blacklist.setAddType(1); // 管理员手动
        blacklist.setStatus(1); // 生效中
        blacklistMapper.add(blacklist);
    }

    @Override
    public void update(Blacklist blacklist) {
        blacklistMapper.update(blacklist);
    }

    @Override
    public void deleteById(Integer id) {
        blacklistMapper.deleteById(id);
    }

    @Override
    public boolean isInBlacklist(Integer studentId) {
        Blacklist blacklist = blacklistMapper.getActiveBlacklist(studentId);
        return blacklist != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFromBlacklist(Integer id) {
        Blacklist blacklist = blacklistMapper.getById(id);
        if (blacklist != null) {
            blacklist.setStatus(0); // 已解除
            blacklistMapper.update(blacklist);
        }
    }

    /**
     * 定时任务：每天凌晨1点检查并自动解除过期的黑名单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoRemoveExpiredBlacklist() {
        Date now = new Date();
        List<Blacklist> expiredList = blacklistMapper.getExpiredBlacklists(now);
        
        for (Blacklist blacklist : expiredList) {
            blacklist.setStatus(0); // 已解除
            blacklistMapper.update(blacklist);
        }
    }
}
