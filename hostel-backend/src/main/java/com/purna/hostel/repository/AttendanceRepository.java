package com.purna.hostel.repository;

import com.purna.hostel.entity.Attendance;
import com.purna.hostel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // ✅ Get attendance by date
    List<Attendance> findByDate(LocalDate date);

    // ✅ Check if ANY attendance exists for a date (VERY IMPORTANT)
    boolean existsByDate(LocalDate date);

    // ✅ Count present/absent by date
    long countByDateAndStatus(LocalDate date, String status);

    // ✅ Prevent duplicate per student
    boolean existsByStudentAndDate(User student, LocalDate date);

    // ✅ Student history
    List<Attendance> findByStudent(User student);

    long countByStudent(User student);

    long countByStudentAndStatus(User student, String status);
}