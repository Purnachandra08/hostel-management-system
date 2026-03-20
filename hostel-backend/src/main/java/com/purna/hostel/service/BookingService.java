package com.purna.hostel.service;

import com.purna.hostel.entity.Booking;
import com.purna.hostel.entity.Room;
import com.purna.hostel.entity.User;
import com.purna.hostel.repository.BookingRepository;
import com.purna.hostel.repository.RoomRepository;
import com.purna.hostel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    // =========================
    // 🔥 Get current academic year (make dynamic later if needed)
    // =========================
    private String getCurrentAcademicYear() {
        return "2025-2026";
    }

    // =========================
    // ✅ CREATE BOOKING (ACADEMIC YEAR BASED, ROOM CAPACITY)
    // =========================
    public Booking createBooking(Long userId, Long roomId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        String year = getCurrentAcademicYear();

        // 🔥 Prevent duplicate booking for same academic year
        boolean alreadyBooked = bookingRepository.existsByUser_IdAndAcademicYearAndIsActiveTrue(userId, year);
        if (alreadyBooked) {
            throw new RuntimeException("Already booked for this academic year");
        }

        // 🔥 Check current room bookings for the year
        long bookedCount = bookingRepository.countByRoom_IdAndAcademicYearAndIsActiveTrue(roomId, year);
        if (bookedCount >= room.getCapacity()) {
            throw new RuntimeException("Room is full for this academic year");
        }

        // ✅ Create new booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setStatus("ACTIVE");
        booking.setAcademicYear(year);
        booking.setActive(true);

        // 🔥 Update room status if full
        if (bookedCount + 1 >= room.getCapacity()) {
            room.setStatus("FULL");
            roomRepository.save(room);
        }

        return bookingRepository.save(booking);
    }

    // =========================
    // ✅ GET ALL BOOKINGS (ADMIN)
    // =========================
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // =========================
    // ✅ GET STUDENT BOOKING (CURRENT YEAR)
    // =========================
    public Booking getStudentBooking(Long userId) {
        return bookingRepository
                .findByUser_IdAndAcademicYearAndIsActiveTrue(userId, getCurrentAcademicYear())
                .orElseThrow(() -> new RuntimeException("No booking found for this year"));
    }

    // =========================
    // ✅ CANCEL BOOKING
    // =========================
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus("CANCELLED");
        booking.setActive(false);

        Room room = booking.getRoom();
        if (room != null) {
            // Check if room still has active bookings for this year
            long activeBookings = bookingRepository.countByRoom_IdAndAcademicYearAndIsActiveTrue(room.getId(), getCurrentAcademicYear());
            if (activeBookings < room.getCapacity()) {
                room.setStatus("AVAILABLE");
                roomRepository.save(room);
            }
        }

        bookingRepository.save(booking);
    }
}