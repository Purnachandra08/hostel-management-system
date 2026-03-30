package com.purna.hostel.repository;

import com.purna.hostel.entity.MessFee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessFeeRepository extends JpaRepository<MessFee, Long> {

    // =========================
    // GET ALL MONTHS OF USER
    // =========================
    List<MessFee> findByUserId(Long userId);

    List<MessFee> findByUserIdAndAcademicYear(Long userId, String academicYear);

    // =========================
    // SINGLE MONTH CHECK
    // =========================
    Optional<MessFee> findByUserIdAndMonthAndYear(Long userId, int month, int year);

    boolean existsByUserIdAndMonthAndYear(Long userId, int month, int year);

    // =========================
    // STATUS FILTER
    // =========================
    List<MessFee> findByUserIdAndStatus(Long userId, String status);
}