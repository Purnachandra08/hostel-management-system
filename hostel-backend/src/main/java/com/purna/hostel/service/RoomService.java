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

    // ✅ Fetch all rooms
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // ✅ Fetch available rooms
    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatusIgnoreCase("AVAILABLE");
    }

    // ✅ Save or add room
    public Room saveRoom(Room room) {
        // Default status if not set
        if (room.getStatus() == null || room.getStatus().isBlank()) {
            room.setStatus("AVAILABLE");
        }

        // Default capacity if not provided
        if (room.getCapacity() <= 0) {
            room.setCapacity(1);
        }

        // ✅ Use correct field: pricePerMonth
        if (room.getPricePerMonth() <= 0) {
            room.setPricePerMonth(5000);
        }

        return roomRepository.save(room);
    }

    // ✅ Delete room by ID
    public void deleteRoom(Long id) {
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
        }
    }
}
