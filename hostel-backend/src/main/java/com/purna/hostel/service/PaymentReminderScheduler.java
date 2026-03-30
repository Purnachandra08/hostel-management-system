package com.purna.hostel.service;

import com.purna.hostel.entity.Booking;
import com.purna.hostel.entity.MessFee;
import com.purna.hostel.repository.BookingRepository;
import com.purna.hostel.repository.MessFeeRepository;
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
    private MessFeeRepository messFeeRepository;

    @Autowired
    private EmailService emailService;

    // 🔔 Runs daily at 10 AM
    @Scheduled(cron = "0 0 10 * * ?")
    public void sendReminders() {

        int currentMonth = LocalDateTime.now().getMonthValue();
        int currentYear = LocalDateTime.now().getYear();
        String monthName = LocalDateTime.now().getMonth().toString();

        // 🔥 Only active bookings
        List<Booking> bookings = bookingRepository.findAll();

        for (Booking booking : bookings) {

            if (!booking.isActive() || !"APPROVED".equalsIgnoreCase(booking.getStatus())) {
                continue;
            }

            Long userId = booking.getUser().getId();

            // 🔥 Check mess fee for current month
            MessFee mess = messFeeRepository
                    .findByUserIdAndMonthAndYear(userId, currentMonth, currentYear)
                    .orElse(null);

            if (mess != null && "UNPAID".equalsIgnoreCase(mess.getStatus())) {

                try {
                    emailService.sendPaymentReminderEmail(
                            booking.getUser().getEmail(),
                            booking.getUser().getFullName(),
                            monthName
                    );
                } catch (Exception e) {
                    System.out.println("Reminder email failed: " + e.getMessage());
                }
            }
        }
    }
}