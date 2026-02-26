package com.purna.hostel.service;

import com.purna.hostel.entity.Attendance;
import com.purna.hostel.entity.User;
import com.purna.hostel.repository.AttendanceRepository;
import com.purna.hostel.repository.LeaveRequestRepository;
import com.purna.hostel.service.email.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    // =====================================================
    // ✅ MARK ATTENDANCE + SEND ABSENT EMAIL
    // =====================================================
    public void markAttendance(List<Attendance> attendanceList) {

        List<Attendance> newAttendanceList = new ArrayList<>();

        for (Attendance attendance : attendanceList) {

            User student = attendance.getStudent();
            LocalDate date = attendance.getDate();

            boolean alreadyMarked =
                    attendanceRepository.existsByStudentAndDate(student, date);

            if (!alreadyMarked) {

                newAttendanceList.add(attendance);

                // 🚨 SEND EMAIL ONLY IF ABSENT
                if ("ABSENT".equalsIgnoreCase(attendance.getStatus())) {

                    emailService.sendAbsentAttendanceEmail(
                            student.getEmail(),
                            student.getFullName(),
                            date.toString()
                    );
                }
            }
        }

        if (!newAttendanceList.isEmpty()) {
            attendanceRepository.saveAll(newAttendanceList);
        }
    }

    // =====================================================
    // OTHER METHODS (UNCHANGED)
    // =====================================================

    public boolean existsByDate(LocalDate date) {
        return attendanceRepository.existsByDate(date);
    }

    public List<Attendance> getTodayAttendance() {
        return attendanceRepository.findByDate(LocalDate.now());
    }

    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }

    public boolean existsByStudentAndDate(User student, LocalDate date) {
        return attendanceRepository.existsByStudentAndDate(student, date);
    }

    public List<Attendance> getAttendanceByStudent(User student) {
        return attendanceRepository.findByStudent(student);
    }

    public double calculateAttendancePercentage(User student) {

        long totalDays = attendanceRepository.countByStudent(student);
        long presentDays =
                attendanceRepository.countByStudentAndStatus(student, "PRESENT");

        if (totalDays == 0) return 0;

        return (presentDays * 100.0) / totalDays;
    }

    public List<User> getStudentsAvailableForAttendance(LocalDate date) {

        List<User> allStudents = userService.getAllStudents();
        List<User> availableStudents = new ArrayList<>();

        for (User student : allStudents) {

            boolean onLeave =
                    leaveRequestRepository
                            .existsByUserAndStatusAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                                    student,
                                    "APPROVED",
                                    date,
                                    date
                            );

            if (!onLeave) {
                availableStudents.add(student);
            }
        }

        return availableStudents;
    }

    public boolean isStudentOnLeave(User student, LocalDate date) {

        return leaveRequestRepository
                .existsByUserAndStatusAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                        student,
                        "APPROVED",
                        date,
                        date
                );
    }
}