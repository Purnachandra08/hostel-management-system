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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:4200") // frontend Angular
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ Create Booking (POST)
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking bookingRequest) {
        try {
            Optional<User> userOpt = userRepository.findById(bookingRequest.getUser().getId());
            Optional<Room> roomOpt = roomRepository.findById(bookingRequest.getRoom().getId());

            if (userOpt.isEmpty() || roomOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid user or room ID");
            }

            Room room = roomOpt.get();

            // check if room is available
            if (!room.getStatus().equalsIgnoreCase("AVAILABLE")) {
                return ResponseEntity.badRequest().body("Room is not available for booking");
            }

            // create booking
            Booking booking = new Booking();
            booking.setUser(userOpt.get());
            booking.setRoom(room);
            booking.setStartDate(bookingRequest.getStartDate());
            booking.setEndDate(bookingRequest.getEndDate());
            booking.setStatus("ACTIVE");
            bookingRepository.save(booking);

            // mark room as booked
            room.setStatus("BOOKED");
            roomRepository.save(room);

            return ResponseEntity.ok("Room booked successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    // ✅ Get all bookings (GET)
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // ✅ Cancel booking (PUT)
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Booking booking = bookingOpt.get();
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        // make room available again
        Room room = booking.getRoom();
        room.setStatus("AVAILABLE");
        roomRepository.save(room);

        return ResponseEntity.ok("Booking cancelled successfully");
    }
}
