package com.purna.hostel.service;

import com.purna.hostel.entity.Booking;
import com.purna.hostel.repository.BookingRepository;
import com.purna.hostel.repository.PaymentRepository;
import com.purna.hostel.service.email.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PaymentReminderScheduler {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EmailService emailService;

    // 🔔 Runs every day at 10 AM
    @Scheduled(cron = "0 0 10 * * ?")
    public void sendReminders() {

        int month = LocalDateTime.now().getMonthValue();
        int year = LocalDateTime.now().getYear();
        String monthName = LocalDateTime.now().getMonth().toString();

        List<Booking> bookings = bookingRepository.findAll();

        for (Booking booking : bookings) {

            Long userId = booking.getUser().getId();

            boolean paid = paymentRepository
                    .existsByUserIdAndMonthAndYear(userId, month, year);

            if (!paid) {
                emailService.sendPaymentReminderEmail(
                        booking.getUser().getEmail(),
                        booking.getUser().getFullName(),
                        monthName
                );
            }
        }
    }
}