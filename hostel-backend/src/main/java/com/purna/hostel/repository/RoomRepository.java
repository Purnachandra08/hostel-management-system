package com.purna.hostel.repository;

import com.purna.hostel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // ✅ Custom finder for available rooms
    List<Room> findByStatusIgnoreCase(String status);
}
