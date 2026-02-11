package com.purna.hostel.service;

import com.purna.hostel.entity.Booking;
import com.purna.hostel.entity.User;
import com.purna.hostel.entity.Room;
import com.purna.hostel.repository.BookingRepository;
import com.purna.hostel.repository.UserRepository;
import com.purna.hostel.repository.RoomRepository;
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

    // ✅ Create new booking
    public Booking createBooking(Long userId, Long roomId, Booking booking) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // ✅ Check availability
        if (!"AVAILABLE".equalsIgnoreCase(room.getStatus())) {
            throw new RuntimeException("Room is not available for booking");
        }

        // ✅ Mark as occupied and save
        room.setStatus("OCCUPIED");
        roomRepository.save(room);

        booking.setUser(user);
        booking.setRoom(room);
        booking.setStatus("ACTIVE");

        return bookingRepository.save(booking);
    }

    // ✅ Get all bookings
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // ✅ Cancel booking and free the room
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus("CANCELLED");

        Room room = booking.getRoom();
        if (room != null) {
            room.setStatus("AVAILABLE");
            roomRepository.save(room);
        }

        bookingRepository.save(booking);
    }
}
