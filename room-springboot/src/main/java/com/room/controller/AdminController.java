package com.room.controller;

import com.room.service.ReservationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@CrossOrigin
@RequestMapping("/admin")
public class AdminController {


    @Resource
    private ReservationService reservationService;



}
