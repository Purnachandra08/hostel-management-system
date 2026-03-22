package com.purna.hostel.repository;

import com.purna.hostel.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByUserId(Long userId);

    List<Payment> findByUserIdOrderByPaymentDateDesc(Long userId);

    // 🔥 MONTHLY CHECK
    Optional<Payment> findByUserIdAndMonthAndYear(Long userId, int month, int year);

    boolean existsByUserIdAndMonthAndYear(Long userId, int month, int year);

    // ADMIN
    List<Payment> findByYear(int year);

    long countByPaymentStatusIgnoreCase(String status);
}