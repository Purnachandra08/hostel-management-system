package com.purna.hostel.service;

import com.purna.hostel.entity.Booking;
import com.purna.hostel.entity.MessFee;
import com.purna.hostel.entity.Payment;
import com.purna.hostel.repository.BookingRepository;
import com.purna.hostel.repository.MessFeeRepository;
import com.purna.hostel.repository.PaymentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    // 🔥 CURRENT ACADEMIC YEAR
    // =========================
    private String getCurrentAcademicYear() {
        return "2025-2026"; // can make dynamic later
    }

    // =========================
    // 🔥 GET ACTIVE BOOKING (CURRENT YEAR)
    // =========================
    private Booking getActiveBooking(Long userId) {
        return bookingRepository
                .findByUser_IdAndAcademicYearAndIsActiveTrue(userId, getCurrentAcademicYear())
                .orElseThrow(() -> new RuntimeException("No active booking found for this year"));
    }

    // =========================
    // 🔥 GET MESS FEE
    // =========================
    private double getMessFee() {
        List<MessFee> fees = messFeeRepository.findAll();
        if (fees.isEmpty()) {
            throw new RuntimeException("Mess fee not set");
        }
        return fees.get(0).getAmount();
    }

    // =========================
    // ✅ CALCULATE FEE (WITHOUT PAYMENT)
    // =========================
    public Payment calculateFee(Long userId) {
        Booking booking = getActiveBooking(userId);

        double roomFee = booking.getRoom().getPricePerMonth();
        double messFee = getMessFee();
        double total = roomFee + messFee;

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUserId(userId);
        payment.setAcademicYear(getCurrentAcademicYear());
        payment.setRoomFee(roomFee);
        payment.setMessFee(messFee);
        payment.setTotalFee(total);
        payment.setPaymentStatus("PENDING");
        payment.setPaymentDate(null); // not paid yet

        return payment;
    }

    // =========================
    // ✅ MAKE PAYMENT
    // =========================
    public Payment makePayment(Long userId) {
        String year = getCurrentAcademicYear();

        // 🔥 Prevent duplicate payment
        paymentRepository.findByUserIdAndAcademicYear(userId, year)
                .ifPresent(p -> {
                    throw new RuntimeException("Already paid for this academic year");
                });

        Booking booking = getActiveBooking(userId);

        double roomFee = booking.getRoom().getPricePerMonth();
        double messFee = getMessFee();
        double total = roomFee + messFee;

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUserId(userId);
        payment.setAcademicYear(year);
        payment.setRoomFee(roomFee);
        payment.setMessFee(messFee);
        payment.setTotalFee(total);
        payment.setPaymentStatus("PAID");
        payment.setPaymentDate(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    // =========================
    // ✅ GET STUDENT PAYMENT HISTORY
    // =========================
    public List<Payment> getStudentPayments(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    // =========================
    // ✅ GET ALL PAYMENTS (ADMIN)
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
            MessFee newFee = new MessFee();
            newFee.setAmount(amount);
            return messFeeRepository.save(newFee);
        }

        MessFee fee = fees.get(0);
        fee.setAmount(amount);
        return messFeeRepository.save(fee);
    }
}