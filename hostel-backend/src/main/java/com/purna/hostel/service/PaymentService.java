package com.purna.hostel.service;

import com.purna.hostel.entity.*;
import com.purna.hostel.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MessFeeRepository messFeeRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // =========================
    // 🔥 COMMON METHOD (GET ACTIVE BOOKING)
    // =========================
    private Booking getActiveBooking(Long userId) {

        List<Booking> bookings =
                bookingRepository.findByUser_IdAndStatusIgnoreCase(userId, "ACTIVE");

        if (bookings.isEmpty()) {
            throw new RuntimeException("No active booking found");
        }

        // ✅ Get latest booking
        return bookings.stream()
                .max(Comparator.comparing(Booking::getId))
                .orElseThrow(() -> new RuntimeException("Booking error"));
    }

    // =========================
    // 🔥 GET MESS FEE SAFELY
    // =========================
    private double getMessFee() {

        List<MessFee> fees = messFeeRepository.findAll();

        if (fees.isEmpty()) {
            throw new RuntimeException("Mess fee not set by admin");
        }

        return fees.get(0).getAmount();
    }

    // =========================
    // ✅ GET FEE DETAILS (PREVIEW)
    // =========================
    public Payment calculateFee(Long userId) {

        Booking booking = getActiveBooking(userId);

        double roomFee = booking.getRoom().getPricePerMonth();
        double messFee = getMessFee();
        double total = roomFee + messFee;

        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setBookingId(booking.getId());
        payment.setRoomFee(roomFee);
        payment.setMessFee(messFee);
        payment.setTotalFee(total);
        payment.setPaymentStatus("PENDING");

        return payment;
    }

    // =========================
    // ✅ PAY FEE
    // =========================
    public Payment makePayment(Long userId) {

        // ❌ Prevent duplicate payment
        List<Payment> existing =
                paymentRepository.findByUserIdAndPaymentStatus(userId, "PAID");

        if (!existing.isEmpty()) {
            throw new RuntimeException("Fee already paid!");
        }

        Booking booking = getActiveBooking(userId);

        double roomFee = booking.getRoom().getPricePerMonth();
        double messFee = getMessFee();
        double total = roomFee + messFee;

        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setBookingId(booking.getId());
        payment.setRoomFee(roomFee);
        payment.setMessFee(messFee);
        payment.setTotalFee(total);
        payment.setPaymentStatus("PAID");
        payment.setPaymentDate(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    // =========================
    // ✅ PAYMENT HISTORY
    // =========================
    public List<Payment> getStudentPayments(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    // =========================
    // ✅ ADMIN - ALL PAYMENTS
    // =========================
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // =========================
    // ✅ UPDATE MESS FEE
    // =========================
    public MessFee updateMessFee(double amount) {

        List<MessFee> fees = messFeeRepository.findAll();

        if (fees.isEmpty()) {
            // 👉 create new if not exists
            MessFee newFee = new MessFee();
            newFee.setAmount(amount);
            return messFeeRepository.save(newFee);
        }

        MessFee fee = fees.get(0);
        fee.setAmount(amount);

        return messFeeRepository.save(fee);
    }
}