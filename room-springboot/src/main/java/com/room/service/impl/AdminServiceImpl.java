package com.room.service.impl;

import com.room.entity.pojo.Admin;
import com.room.exception.BusinessException;
import com.room.mapper.AdminMapper;
import com.room.service.AdminService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Override
    public Admin login(String account, String password) {
        Admin dbAdmin = adminMapper.getByAccount(account);
        if (dbAdmin == null){
            throw new BusinessException("账号不存在");
        }
        if (!dbAdmin.getPassword().equals(password)){
            throw new BusinessException("密码错误");
        }
        return dbAdmin;
    }
}
