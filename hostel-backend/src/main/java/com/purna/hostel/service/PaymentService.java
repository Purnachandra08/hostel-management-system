package com.purna.hostel.service;

import com.purna.hostel.entity.*;
import com.purna.hostel.repository.*;
import com.purna.hostel.service.email.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MessFeeRepository messFeeRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EmailService emailService;

    // =========================
    // 🔥 FIXED: GET ACTIVE BOOKING (NO ERROR)
    // =========================
    private Booking getActiveBooking(Long userId) {

        List<Booking> bookings =
                bookingRepository.findAllByUser_IdAndIsActiveTrue(userId);

        if (bookings.isEmpty()) {
            throw new RuntimeException("No active booking found");
        }

        return bookings.get(0); // ✅ FIX: avoid "non-unique result"
    }

    // =========================
    // 🏠 ROOM PAYMENT
    // =========================
    @Transactional
    public Payment payRoomFee(Long userId) {

        Booking booking = getActiveBooking(userId);

        // ✅ FIX: handle correct statuses
        if (!"PENDING".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("Booking already processed or paid");
        }

        // ❌ FIX: avoid duplicate payment
        if (paymentRepository.existsByReferenceId(booking.getId())) {
            throw new RuntimeException("Room fee already paid");
        }

        double amount = booking.getRoom().getPricePerMonth() * 12;

        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setType("ROOM");
        payment.setReferenceId(booking.getId());
        payment.setAmount(amount);
        payment.setStatus("SUCCESS");
        payment.setTransactionId(UUID.randomUUID().toString());

        // ❌ REMOVE manual date (handled by @PrePersist)
        // payment.setPaymentDate(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        // =========================
        // ✅ APPROVE BOOKING
        // =========================
        booking.setStatus("APPROVED");

        Room room = booking.getRoom();
        room.setOccupiedCount(room.getOccupiedCount() + 1);

        if (room.getOccupiedCount() >= room.getCapacity()) {
            room.setStatus("FULL");
        }

        roomRepository.save(room);
        bookingRepository.save(booking);

        // =========================
        // ✅ CREATE MESS FEES
        // =========================
        createYearlyMessFees(userId, booking.getAcademicYear(), 3000);

        // =========================
        // 📧 EMAIL
        // =========================
        try {
            emailService.sendPaymentSuccessEmail(
                    booking.getUser().getEmail(),
                    booking.getUser().getFullName(),
                    "Room Fee",
                    amount
            );
        } catch (Exception e) {
            System.out.println("Email failed: " + e.getMessage());
        }

        return saved;
    }

    // =========================
    // 🍽️ PAY MESS FEE
    // =========================
    @Transactional
    public Payment payMessFee(Long messFeeId) {

        MessFee mess = messFeeRepository.findById(messFeeId)
                .orElseThrow(() -> new RuntimeException("Mess fee not found"));

        if ("PAID".equalsIgnoreCase(mess.getStatus())) {
            throw new RuntimeException("Already paid");
        }

        Payment payment = new Payment();
        payment.setUserId(mess.getUserId());
        payment.setType("MESS");
        payment.setReferenceId(mess.getId());
        payment.setAmount(mess.getAmount());
        payment.setStatus("SUCCESS");
        payment.setTransactionId(UUID.randomUUID().toString());

        Payment saved = paymentRepository.save(payment);

        mess.setStatus("PAID");
        messFeeRepository.save(mess);

        return saved;
    }

    // =========================
    // 📜 HISTORY
    // =========================
    public List<Payment> getUserPayments(Long userId) {
        return paymentRepository.findAllByUserIdOrderByPaymentDateDesc(userId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // =========================
    // 🍽️ CREATE MESS (AUTO)
    // =========================
    public void createYearlyMessFees(Long userId, String academicYear, double amount) {

        int year = java.time.LocalDate.now().getYear();

        for (int i = 1; i <= 12; i++) {

            if (!messFeeRepository.existsByUserIdAndMonthAndYear(userId, i, year)) {

                MessFee mess = new MessFee();
                mess.setUserId(userId);
                mess.setAcademicYear(academicYear);
                mess.setMonth(i);
                mess.setYear(year);
                mess.setAmount(amount);
                mess.setStatus("UNPAID");

                messFeeRepository.save(mess);
            }
        }
    }
}