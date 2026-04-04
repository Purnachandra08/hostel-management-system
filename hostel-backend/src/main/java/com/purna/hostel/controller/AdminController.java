package com.purna.hostel.controller;

import com.purna.hostel.entity.Payment;
import com.purna.hostel.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    @Autowired
    private PaymentService paymentService;

    // =========================
    // 📊 GET ALL PAYMENTS (ADMIN)
    // =========================
    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
}