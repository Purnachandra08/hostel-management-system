package com.purna.hostel.service;

import com.purna.hostel.entity.Booking;
import com.purna.hostel.entity.MessFee;
import com.purna.hostel.entity.Payment;
import com.purna.hostel.repository.BookingRepository;
import com.purna.hostel.repository.MessFeeRepository;
import com.purna.hostel.repository.PaymentRepository;
import com.purna.hostel.service.email.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MessFeeRepository messFeeRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // =========================
    // 🔥 CURRENT DATE
    // =========================
    private int getCurrentMonth() {
        return LocalDateTime.now().getMonthValue();
    }

    private int getCurrentYear() {
        return LocalDateTime.now().getYear();
    }

    private String getCurrentAcademicYear() {
        return "2025-2026";
    }

    // =========================
    // 🔥 GET ACTIVE BOOKING
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
        if (fees.isEmpty()) throw new RuntimeException("Mess fee not set");
        return fees.get(0).getAmount();
    }

    // =========================
    // ✅ CALCULATE FEE (MONTHLY)
    // =========================
    public Payment calculateFee(Long userId) {

        int month = getCurrentMonth();
        int year = getCurrentYear();

        Booking booking = getActiveBooking(userId);

        double roomFee = booking.getRoom().getPricePerMonth();
        double messFee = getMessFee();

        Payment existing = paymentRepository
                .findByUserIdAndMonthAndYear(userId, month, year)
                .orElse(null);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUserId(userId);
        payment.setAcademicYear(getCurrentAcademicYear());
        payment.setMonth(month);
        payment.setYear(year);
        payment.setRoomFee(roomFee);
        payment.setMessFee(messFee);
        payment.setTotalFee(roomFee + messFee);

        if (existing != null) {
            payment.setPaymentStatus(existing.getPaymentStatus());
            payment.setPaymentDate(existing.getPaymentDate());
        } else {
            payment.setPaymentStatus("PENDING");
        }

        return payment;
    }

    // =========================
    // ✅ MAKE PAYMENT (MONTHLY + EMAIL)
    // =========================
    public Payment makePayment(Long userId) {

        int month = getCurrentMonth();
        int year = getCurrentYear();

        // ❌ Prevent duplicate payment
        if (paymentRepository.existsByUserIdAndMonthAndYear(userId, month, year)) {
            throw new RuntimeException("Already paid for this month");
        }

        Booking booking = getActiveBooking(userId);

        double roomFee = booking.getRoom().getPricePerMonth();
        double messFee = getMessFee();

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUserId(userId);
        payment.setAcademicYear(getCurrentAcademicYear());
        payment.setMonth(month);
        payment.setYear(year);
        payment.setRoomFee(roomFee);
        payment.setMessFee(messFee);
        payment.setTotalFee(roomFee + messFee);
        payment.setPaymentStatus("PAID");
        payment.setPaymentDate(LocalDateTime.now());

        // ✅ SAVE FIRST
        Payment savedPayment = paymentRepository.save(payment);

        // =========================
        // 📧 SEND PAYMENT EMAIL
        // =========================
        try {
            String email = booking.getUser().getEmail();
            String name = booking.getUser().getFullName();
            String monthName = LocalDateTime.now().getMonth().toString();

            emailService.sendPaymentSuccessEmail(
                    email,
                    name,
                    monthName,
                    savedPayment.getTotalFee()
            );
        } catch (Exception e) {
            // ❗ Do not break payment if email fails
            System.out.println("Email sending failed: " + e.getMessage());
        }

        return savedPayment;
    }

    // =========================
    // ✅ HISTORY
    // =========================
    public List<Payment> getStudentPayments(Long userId) {
        return paymentRepository.findByUserIdOrderByPaymentDateDesc(userId);
    }

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