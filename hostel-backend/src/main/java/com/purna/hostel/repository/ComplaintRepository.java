package com.purna.hostel.repository;

import com.purna.hostel.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // Fetch complaints by user ID (based on ManyToOne mapping)
    List<Complaint> findByUser_Id(Long userId);
}