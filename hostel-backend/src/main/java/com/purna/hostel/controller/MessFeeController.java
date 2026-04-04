package com.purna.hostel.controller;

import com.purna.hostel.entity.MessFee;
import com.purna.hostel.repository.MessFeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mess-fees")
@CrossOrigin(origins = "http://localhost:4200")
public class MessFeeController {

    @Autowired
    private MessFeeRepository messFeeRepository;

    // ✅ GET ALL MESS FEES FOR USER
    @GetMapping("/{userId}")
    public List<MessFee> getUserMessFees(@PathVariable Long userId) {
        return messFeeRepository.findByUserId(userId);
    }
}