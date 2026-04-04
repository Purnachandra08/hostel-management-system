package com.purna.hostel.repository;

import com.purna.hostel.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // =========================
    // USER PAYMENTS
    // =========================
    List<Payment> findByUserId(Long userId);

    // ✅ SAFE VERSION (NO ERROR)
    List<Payment> findAllByUserIdOrderByPaymentDateDesc(Long userId);

    // =========================
    // TYPE BASED (ROOM / MESS)
    // =========================
    List<Payment> findByUserIdAndType(Long userId, String type);

    // =========================
    // REFERENCE CHECK
    // =========================
    Optional<Payment> findByReferenceId(Long referenceId);

    boolean existsByReferenceId(Long referenceId);

    // =========================
    // ADMIN REPORTS
    // =========================
    List<Payment> findByType(String type);

    long countByStatusIgnoreCase(String status);
}