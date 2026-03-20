package com.purna.hostel.repository;

import com.purna.hostel.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // =========================
    // BASIC
    // =========================
    List<Payment> findByUserId(Long userId);

    List<Payment> findByUserIdAndPaymentStatus(Long userId, String status);

    // =========================
    // 🎯 ACADEMIC YEAR LOGIC
    // =========================

    // ✅ Check if already paid for a year
    Optional<Payment> findByUserIdAndAcademicYear(Long userId, String academicYear);

    // ✅ Strict check (PAID only)
    boolean existsByUserIdAndAcademicYearAndPaymentStatusIgnoreCase(
            Long userId,
            String academicYear,
            String status
    );

    // ✅ Get all payments by year (admin)
    List<Payment> findByAcademicYear(String academicYear);

    // =========================
    // 🔥 ADMIN STATS
    // =========================

    long countByPaymentStatusIgnoreCase(String status);
}