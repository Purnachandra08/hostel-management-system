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
    // CURRENT YEAR
    // =========================
    private String getCurrentAcademicYear() {
        return "2025-2026";
    }

    // =========================
    // CREATE BOOKING (PENDING)
    // =========================
    public Booking createBooking(Long userId, Long roomId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        String year = getCurrentAcademicYear();

        // ❌ One booking per year
        if (bookingRepository.existsByUser_IdAndAcademicYearAndIsActiveTrue(userId, year)) {
            throw new RuntimeException("Already booked for this year");
        }

        // ❌ Check room capacity
        if (room.getOccupiedCount() >= room.getCapacity()) {
            throw new RuntimeException("Room is full");
        }

        // ✅ Create booking (PENDING)
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setAcademicYear(year);
        booking.setStatus("PENDING");
        booking.setActive(true);

        return bookingRepository.save(booking);
    }

    // =========================
    // APPROVE BOOKING (AFTER PAYMENT)
    // =========================
    public Booking approveBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if ("APPROVED".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("Already approved");
        }

        Room room = booking.getRoom();

        // ❌ Check again before approving
        if (room.getOccupiedCount() >= room.getCapacity()) {
            throw new RuntimeException("Room full");
        }

        // ✅ Update booking
        booking.setStatus("APPROVED");

        // ✅ Update room
        room.setOccupiedCount(room.getOccupiedCount() + 1);

        if (room.getOccupiedCount() >= room.getCapacity()) {
            room.setStatus("FULL");
        }

        roomRepository.save(room);

        return bookingRepository.save(booking);
    }

    // =========================
    // GET STUDENT BOOKING
    // =========================
    public Booking getStudentBooking(Long userId) {
        return bookingRepository
                .findByUser_IdAndAcademicYearAndIsActiveTrue(userId, getCurrentAcademicYear())
                .orElseThrow(() -> new RuntimeException("No booking found"));
    }

    // =========================
    // ADMIN VIEW
    // =========================
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // =========================
    // CANCEL BOOKING
    // =========================
    public void cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("Already cancelled");
        }

        booking.setStatus("CANCELLED");
        booking.setActive(false);

        Room room = booking.getRoom();

        // 🔥 Reduce occupied count only if approved
        if ("APPROVED".equalsIgnoreCase(booking.getStatus())) {
            room.setOccupiedCount(Math.max(0, room.getOccupiedCount() - 1));
        }

        if (room.getOccupiedCount() < room.getCapacity()) {
            room.setStatus("AVAILABLE");
        }

        roomRepository.save(room);
        bookingRepository.save(booking);
    }
}