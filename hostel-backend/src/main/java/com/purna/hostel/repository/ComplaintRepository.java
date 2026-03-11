package com.purna.hostel.repository;

import com.purna.hostel.entity.Complaint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // =========================
    // FETCH COMPLAINTS BY USER
    // =========================
    List<Complaint> findByUser_Id(Long userId);

    // =========================
    // ADMIN ANALYTICS METHODS
    // =========================
    long countByStatusIgnoreCase(String status);

    List<Complaint> findByStatusIgnoreCase(String status);

}