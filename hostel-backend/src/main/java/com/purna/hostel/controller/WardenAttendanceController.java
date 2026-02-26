package com.purna.hostel.controller;

import com.purna.hostel.entity.Attendance;
import com.purna.hostel.entity.User;
import com.purna.hostel.service.AttendanceService;
import com.purna.hostel.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/warden")
@CrossOrigin(origins = "http://localhost:4200")
public class WardenAttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserService userService;

    // =====================================================
    // ✅ GET STUDENTS AVAILABLE FOR ATTENDANCE (DATE-WISE)
    // =====================================================
    @GetMapping("/attendance/students/{date}")
    public ResponseEntity<?> getStudentsForAttendance(@PathVariable String date) {
        try {
            LocalDate selectedDate = LocalDate.parse(date);

            List<User> students =
                    attendanceService.getStudentsAvailableForAttendance(selectedDate);

            return ResponseEntity.ok(students);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid date format. Use yyyy-MM-dd"));
        }
    }

    // =====================================================
    // ✅ MARK ATTENDANCE
    // =====================================================
    @PostMapping("/attendance/mark")
    public ResponseEntity<?> markAttendance(@RequestBody List<Map<String, Object>> attendanceList) {

        if (attendanceList == null || attendanceList.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "⚠️ No attendance data provided"));
        }

        try {

            // 🔥 Validate first record date
            if (!attendanceList.get(0).containsKey("date")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Date is required"));
            }

            LocalDate attendanceDate =
                    LocalDate.parse(attendanceList.get(0).get("date").toString());

            // 🔒 LOCK DATE IF ALREADY MARKED
            if (attendanceService.existsByDate(attendanceDate)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error",
                                "⚠️ Attendance already submitted for " + attendanceDate));
            }

            List<Attendance> attendanceRecords = new ArrayList<>();

            for (Map<String, Object> record : attendanceList) {

                if (!record.containsKey("studentId") ||
                    !record.containsKey("status")) {
                    continue;
                }

                Long studentId = Long.valueOf(record.get("studentId").toString());
                String status = record.get("status").toString().toUpperCase();

                // ✅ Validate status
                if (!status.equals("PRESENT") && !status.equals("ABSENT")) {
                    continue;
                }

                // ✅ Ensure all dates match
                LocalDate recordDate =
                        LocalDate.parse(record.get("date").toString());

                if (!recordDate.equals(attendanceDate)) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error",
                                    "All records must have same date"));
                }

                User student = userService.getUserById(studentId);

                if (student == null) {
                    continue;
                }

                // 🚫 Skip student on approved leave
                if (attendanceService.isStudentOnLeave(student, attendanceDate)) {
                    continue;
                }

                Attendance attendance = new Attendance();
                attendance.setStudent(student);
                attendance.setDate(attendanceDate);
                attendance.setStatus(status);

                attendanceRecords.add(attendance);
            }

            if (attendanceRecords.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error",
                                "⚠️ No valid students to mark"));
            }

            attendanceService.markAttendance(attendanceRecords);

            return ResponseEntity.ok(
                    Map.of(
                            "message", "✅ Attendance marked successfully",
                            "count", attendanceRecords.size(),
                            "date", attendanceDate.toString()
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid attendance data format"));
        }
    }

    // =====================================================
    // ✅ GET TODAY'S ATTENDANCE
    // =====================================================
    @GetMapping("/attendance/today")
    public ResponseEntity<?> getTodayAttendance() {

        LocalDate today = LocalDate.now();
        List<Attendance> todayRecords =
                attendanceService.getAttendanceByDate(today);

        return ResponseEntity.ok(todayRecords);
    }

    // =====================================================
    // ✅ GET ATTENDANCE BY DATE
    // =====================================================
    @GetMapping("/attendance/{date}")
    public ResponseEntity<?> getAttendanceByDate(@PathVariable String date) {

        try {
            LocalDate parsedDate = LocalDate.parse(date);

            List<Attendance> records =
                    attendanceService.getAttendanceByDate(parsedDate);

            return ResponseEntity.ok(records);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid date format. Use yyyy-MM-dd"));
        }
    }
}