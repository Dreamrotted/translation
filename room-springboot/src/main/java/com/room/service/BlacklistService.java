package com.room.service;

import com.github.pagehelper.PageInfo;
import com.room.entity.pojo.Blacklist;

public interface BlacklistService {

    PageInfo<Blacklist> page(Integer pageNum, Integer pageSize, Integer studentNumber, Integer status);

    void add(Blacklist blacklist);

    void update(Blacklist blacklist);

    void deleteById(Integer id);

    /**
     * 检查学生是否在黑名单中
     */
    boolean isInBlacklist(Integer studentId);

    /**
     * 解除黑名单
     */
    void removeFromBlacklist(Integer id);
}
