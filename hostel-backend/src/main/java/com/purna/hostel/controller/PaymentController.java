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
    // ✅ GET FEE DETAILS
    // =========================
    @GetMapping("/fee/{userId}")
    public ResponseEntity<?> getFee(@PathVariable Long userId) {
        try {
            Payment payment = paymentService.calculateFee(userId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            if (e.getMessage().equals("No active booking found for this year")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Please book a room first"));
            }
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // =========================
    // ✅ PAY FEE
    // =========================
    @PostMapping("/pay/{userId}")
    public ResponseEntity<?> payFee(@PathVariable Long userId) {
        try {
            Payment payment = paymentService.makePayment(userId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // =========================
    // ✅ STUDENT PAYMENT HISTORY
    // =========================
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Payment>> getStudentPayments(@PathVariable Long userId) {
        List<Payment> payments = paymentService.getStudentPayments(userId);
        return ResponseEntity.ok(payments);
    }

    // =========================
    // ✅ ADMIN: ALL PAYMENTS
    // =========================
    @GetMapping("/all")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }
}