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
    // 🔥 ACTIVE BOOKING (IMPORTANT FIX)
    // =========================

    // ✅ SINGLE ACTIVE BOOKING (USE THIS IN SERVICE)
    Optional<Booking> findByUser_IdAndIsActiveTrue(Long userId);

    // ✅ LIST (optional for admin/debug)
    List<Booking> findByUser_IdAndIsActiveTrueOrderByCreatedAtDesc(Long userId);

    // =========================
    // 🎯 ACADEMIC YEAR LOGIC
    // =========================

    boolean existsByUser_IdAndAcademicYearAndIsActiveTrue(Long userId, String academicYear);

    Optional<Booking> findByUser_IdAndAcademicYearAndIsActiveTrue(Long userId, String academicYear);

    List<Booking> findByAcademicYear(String academicYear);

    // =========================
    // 🔥 ROOM CAPACITY LOGIC
    // =========================

    long countByRoom_IdAndAcademicYearAndIsActiveTrue(Long roomId, String academicYear);

    List<Booking> findByRoom_IdAndIsActiveTrue(Long roomId);
}