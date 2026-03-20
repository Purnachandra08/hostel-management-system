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

    // ✅ Get all rooms
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // ✅ Get available rooms (based on capacity)
    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatusIgnoreCase("AVAILABLE");
    }

    // ✅ Add or update room
    public Room saveRoom(Room room) {
        if (room.getStatus() == null || room.getStatus().isBlank()) room.setStatus("AVAILABLE");
        if (room.getCapacity() <= 0) room.setCapacity(1);
        if (room.getPricePerMonth() <= 0) room.setPricePerMonth(5000);

        return roomRepository.save(room);
    }

    // ✅ Delete room
    public void deleteRoom(Long id) {
        if (roomRepository.existsById(id)) roomRepository.deleteById(id);
    }
}