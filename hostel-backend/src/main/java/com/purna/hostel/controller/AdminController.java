package com.purna.hostel.controller;

import com.purna.hostel.entity.MessFee;
import com.purna.hostel.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    @Autowired
    private PaymentService paymentService;

    // =========================
    // ✅ UPDATE MESS FEE
    // =========================
    @PostMapping("/mess-fee")
    public MessFee updateMessFee(@RequestParam double amount) {
        return paymentService.updateMessFee(amount);
    }
}