package com.purna.hostel.repository;

import com.purna.hostel.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByUserId(Long userId);
    List<Payment> findByUserIdAndPaymentStatus(Long userId, String status);
}
