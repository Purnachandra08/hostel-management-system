package com.purna.hostel.controller;

import com.purna.hostel.entity.Booking;
import com.purna.hostel.entity.Room;
import com.purna.hostel.entity.User;
import com.purna.hostel.repository.BookingRepository;
import com.purna.hostel.repository.RoomRepository;
import com.purna.hostel.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:4200")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    // ================================
    // ✅ CREATE BOOKING
    // ================================
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking bookingRequest) {

        try {

            if (bookingRequest.getUser() == null || bookingRequest.getRoom() == null) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("message", "User or Room missing in request"));
            }

            Optional<User> userOpt = userRepository.findById(bookingRequest.getUser().getId());
            Optional<Room> roomOpt = roomRepository.findById(bookingRequest.getRoom().getId());

            if (userOpt.isEmpty() || roomOpt.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("message", "Invalid user or room ID"));
            }

            Room room = roomOpt.get();

            // Check if room is available
            if (!room.getStatus().equalsIgnoreCase("AVAILABLE")) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("message", "Room is not available for booking"));
            }

            // Create booking
            Booking booking = new Booking();
            booking.setUser(userOpt.get());
            booking.setRoom(room);
            booking.setStartDate(bookingRequest.getStartDate());
            booking.setEndDate(bookingRequest.getEndDate());
            booking.setStatus("ACTIVE");

            bookingRepository.save(booking);

            // Update room status
            room.setStatus("BOOKED");
            roomRepository.save(room);

            return ResponseEntity.ok(
                    Map.of("message", "Room booked successfully")
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(Map.of("message", "Server error: " + e.getMessage()));
        }
    }

    // ================================
    // ✅ GET ALL BOOKINGS
    // ================================
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // ================================
    // ✅ CANCEL BOOKING
    // ================================
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {

        Optional<Booking> bookingOpt = bookingRepository.findById(id);

        if (bookingOpt.isEmpty()) {
            return ResponseEntity
                    .status(404)
                    .body(Map.of("message", "Booking not found"));
        }

        Booking booking = bookingOpt.get();
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        // Make room available again
        Room room = booking.getRoom();
        room.setStatus("AVAILABLE");
        roomRepository.save(room);

        return ResponseEntity.ok(
                Map.of("message", "Booking cancelled successfully")
        );
    }
}