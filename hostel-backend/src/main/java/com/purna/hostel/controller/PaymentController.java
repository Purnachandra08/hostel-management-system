package com.purna.hostel.controller;

import com.purna.hostel.entity.Payment;
import com.purna.hostel.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin("*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // ✅ GET FEE DETAILS
    @GetMapping("/fee/{userId}")
    public Payment getFee(@PathVariable Long userId) {
        return paymentService.calculateFee(userId);
    }

    // ✅ PAY FEE
    @PostMapping("/pay/{userId}")
    public Payment payFee(@PathVariable Long userId) {
        return paymentService.makePayment(userId);
    }

    // ✅ PAYMENT HISTORY (FIXED)
    @GetMapping("/history/{userId}")
    public List<Payment> getStudentPayments(@PathVariable Long userId) {
        return paymentService.getStudentPayments(userId);
    }

    // ✅ ADMIN: ALL PAYMENTS
    @GetMapping("/all")
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }
}