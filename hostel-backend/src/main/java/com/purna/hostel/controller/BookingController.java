package com.purna.hostel.controller;

import com.purna.hostel.entity.Booking;
import com.purna.hostel.service.BookingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:4200")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // ================================
    // 🧑‍🎓 CREATE BOOKING (PENDING)
    // ================================
    @PostMapping("/{userId}/{roomId}")
    public ResponseEntity<?> createBooking(
            @PathVariable Long userId,
            @PathVariable Long roomId
    ) {
        try {
            Booking booking = bookingService.createBooking(userId, roomId);

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Booking created. Please complete payment.",
                            "bookingId", booking.getId()
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ================================
    // ✅ APPROVE BOOKING (ADMIN / PAYMENT)
    // ================================
    @PutMapping("/approve/{bookingId}")
    public ResponseEntity<?> approveBooking(@PathVariable Long bookingId) {
        try {
            Booking booking = bookingService.approveBooking(bookingId);
            return ResponseEntity.ok(booking);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ================================
    // 📋 ALL BOOKINGS (ADMIN)
    // ================================
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    // ================================
    // 👤 STUDENT BOOKING
    // ================================
    @GetMapping("/student/{userId}")
    public ResponseEntity<?> getStudentBooking(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(bookingService.getStudentBooking(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ================================
    // ❌ CANCEL BOOKING
    // ================================
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            bookingService.cancelBooking(id);
            return ResponseEntity.ok(Map.of("message", "Booking cancelled"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}