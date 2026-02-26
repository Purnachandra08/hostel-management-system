package com.purna.hostel.repository;

import com.purna.hostel.entity.LeaveRequest;
import com.purna.hostel.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    // ✅ Get all leaves of a user
    List<LeaveRequest> findByUserId(Long userId);

    // ✅ Count leaves by status
    long countByStatus(String status);

    // ✅ VERY IMPORTANT (For Attendance Filtering)
    boolean existsByUserAndStatusAndFromDateLessThanEqualAndToDateGreaterThanEqual(
            User user,
            String status,
            LocalDate fromDate,
            LocalDate toDate
    );

    // ✅ Optional: Get approved leaves by date
    List<LeaveRequest> findByStatusAndFromDateLessThanEqualAndToDateGreaterThanEqual(
            String status,
            LocalDate fromDate,
            LocalDate toDate
    );
}