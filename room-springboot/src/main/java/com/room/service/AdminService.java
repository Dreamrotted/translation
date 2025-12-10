package com.room.service;

import com.room.entity.pojo.Admin;

public interface AdminService {

    Admin login(String account, String password);
}