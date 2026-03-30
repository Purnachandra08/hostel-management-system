package com.purna.hostel.repository;

import com.purna.hostel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // =========================
    // AVAILABLE ROOMS
    // =========================
    List<Room> findByStatusIgnoreCase(String status);

    // =========================
    // DUPLICATE CHECK 🔥
    // =========================
    Optional<Room> findByRoomNumber(String roomNumber);

    boolean existsByRoomNumber(String roomNumber);

    // =========================
    // ADMIN DASHBOARD
    // =========================
    long countByStatusIgnoreCase(String status);
}