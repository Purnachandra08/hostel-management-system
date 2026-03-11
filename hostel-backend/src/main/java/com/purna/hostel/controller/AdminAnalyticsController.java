package com.purna.hostel.controller;

import com.purna.hostel.dto.AdminDashboardStats;
import com.purna.hostel.service.AdminAnalyticsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminAnalyticsController {

    @Autowired
    private AdminAnalyticsService adminAnalyticsService;

    @GetMapping("/analytics")
    public AdminDashboardStats getAnalytics() {
        return adminAnalyticsService.getAnalytics();
    }
}