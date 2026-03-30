package com.purna.hostel.controller;

import com.purna.hostel.entity.Payment;
import com.purna.hostel.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // =========================
    // 🏠 PAY ROOM FEE
    // =========================
    @PostMapping("/room/{userId}")
    public ResponseEntity<?> payRoomFee(@PathVariable Long userId) {
        try {
            Payment payment = paymentService.payRoomFee(userId);
            return ResponseEntity.ok(payment);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // =========================
    // 🍽️ PAY MESS FEE
    // =========================
    @PostMapping("/mess/{messId}")
    public ResponseEntity<?> payMessFee(@PathVariable Long messId) {
        try {
            Payment payment = paymentService.payMessFee(messId);
            return ResponseEntity.ok(payment);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // =========================
    // 📜 USER PAYMENT HISTORY
    // =========================
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Payment>> getUserPayments(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getUserPayments(userId));
    }

    // =========================
    // 📊 ADMIN: ALL PAYMENTS
    // =========================
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
}