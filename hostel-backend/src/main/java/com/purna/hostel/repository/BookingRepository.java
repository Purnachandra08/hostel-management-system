package com.purna.hostel.repository;

import com.purna.hostel.entity.Booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // =========================
    // FIND BOOKINGS BY USER
    // =========================
    List<Booking> findByUser_Id(Long userId);

    // =========================
    // FIND BOOKINGS BY ROOM
    // =========================
    List<Booking> findByRoom_Id(Long roomId);
    
    List<Booking> findByUser_IdAndStatusIgnoreCase(Long userId, String status);
    // =========================
    // COUNT BOOKINGS BY STATUS
    // =========================
    long countByStatusIgnoreCase(String status);

}