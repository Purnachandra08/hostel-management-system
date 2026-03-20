package com.purna.hostel.controller;

import com.purna.hostel.entity.Room;
import com.purna.hostel.service.RoomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // ✅ ADD THIS IMPORT

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:4200")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // =========================
    // ✅ GET ALL ROOMS
    // =========================
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    // =========================
    // ✅ GET AVAILABLE ROOMS
    // =========================
    @GetMapping("/available")
    public ResponseEntity<List<Room>> getAvailableRooms() {
        return ResponseEntity.ok(roomService.getAvailableRooms());
    }

    // =========================
    // ✅ ADD ROOM
    // =========================
    @PostMapping
    public ResponseEntity<?> addRoom(@RequestBody Room room) {
        try {
            Room savedRoom = roomService.saveRoom(room);
            return ResponseEntity.ok(savedRoom);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Error adding room: " + e.getMessage()));
        }
    }

    // =========================
    // ✅ DELETE ROOM
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id) {
        try {
            roomService.deleteRoom(id);
            return ResponseEntity.ok(Map.of("message", "Room deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Error deleting room: " + e.getMessage()));
        }
    }
}