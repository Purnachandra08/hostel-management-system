package com.purna.hostel.service;

import com.purna.hostel.entity.Room;
import com.purna.hostel.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    // =========================
    // GET ALL ROOMS
    // =========================
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // =========================
    // AVAILABLE ROOMS
    // =========================
    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatusIgnoreCase("AVAILABLE");
    }

    // =========================
    // CREATE ROOM (ADMIN)
    // =========================
    public Room createRoom(Room room) {

        // 🔥 VALIDATIONS
        if (room.getRoomNumber() == null || room.getRoomNumber().isBlank()) {
            throw new RuntimeException("Room number is required");
        }

        if (roomRepository.existsByRoomNumber(room.getRoomNumber())) {
            throw new RuntimeException("Room number already exists");
        }

        if (room.getCapacity() <= 0) {
            throw new RuntimeException("Capacity must be greater than 0");
        }

        if (room.getPricePerMonth() <= 0) {
            throw new RuntimeException("Price must be greater than 0");
        }

        room.setOccupiedCount(0);
        room.setStatus("AVAILABLE");

        return roomRepository.save(room);
    }

    // =========================
    // UPDATE ROOM
    // =========================
    public Room updateRoom(Long id, Room updatedRoom) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // 🔥 DUPLICATE CHECK
        if (!room.getRoomNumber().equals(updatedRoom.getRoomNumber()) &&
                roomRepository.existsByRoomNumber(updatedRoom.getRoomNumber())) {
            throw new RuntimeException("Room number already exists");
        }

        room.setRoomNumber(updatedRoom.getRoomNumber());
        room.setType(updatedRoom.getType());
        room.setCapacity(updatedRoom.getCapacity());
        room.setPricePerMonth(updatedRoom.getPricePerMonth());

        // 🔥 AUTO STATUS UPDATE
        if (room.getOccupiedCount() >= room.getCapacity()) {
            room.setStatus("FULL");
        } else {
            room.setStatus("AVAILABLE");
        }

        return roomRepository.save(room);
    }

    // =========================
    // DELETE ROOM
    // =========================
    public void deleteRoom(Long id) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (!room.getBookings().isEmpty()) {
            throw new RuntimeException("Cannot delete room with bookings");
        }

        roomRepository.delete(room);
    }
}