package com.purna.hostel.controller;

import com.purna.hostel.entity.Attendance;
import com.purna.hostel.service.AttendanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "http://localhost:4200") // allow Angular frontend
public class StudentAttendanceController {

    private final AttendanceService attendanceService;

    public StudentAttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    // =====================================================
    // ✅ GET ALL ATTENDANCE OF A STUDENT
    // =====================================================
    @GetMapping("/{studentId}")
    public List<Attendance> getAttendanceByStudent(@PathVariable Long studentId) {
        return attendanceService.getAttendanceByStudentId(studentId);
    }
}