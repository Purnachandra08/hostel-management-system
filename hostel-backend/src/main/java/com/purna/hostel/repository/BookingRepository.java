package com.purna.hostel.repository;

import com.purna.hostel.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // =========================
    // BASIC FETCH
    // =========================
    List<Booking> findByUser_Id(Long userId);

    List<Booking> findByRoom_Id(Long roomId);

    // =========================
    // STATUS BASED
    // =========================
    List<Booking> findByUser_IdAndStatusIgnoreCase(Long userId, String status);

    long countByStatusIgnoreCase(String status);

    // =========================
    // 🎯 ACADEMIC YEAR LOGIC
    // =========================

    // ✅ Check if already booked (STRICT RULE)
    boolean existsByUser_IdAndAcademicYearAndIsActiveTrue(Long userId, String academicYear);

    // ✅ Get active booking for a student
    Optional<Booking> findByUser_IdAndAcademicYearAndIsActiveTrue(Long userId, String academicYear);

    // ✅ Get all bookings of a year
    List<Booking> findByAcademicYear(String academicYear);

    // =========================
    // 🔥 ROOM CAPACITY LOGIC
    // =========================

    // ✅ Count students in a room for a year
    long countByRoom_IdAndAcademicYearAndIsActiveTrue(Long roomId, String academicYear);

    // ✅ Get all active bookings of a room (for admin view)
    List<Booking> findByRoom_IdAndIsActiveTrue(Long roomId);
}