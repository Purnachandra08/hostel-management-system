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

    /**
     * ✅ Mark attendance for one or multiple students
     */
    @PostMapping("/attendance/mark")
    public ResponseEntity<?> markAttendance(@RequestBody List<Map<String, Object>> attendanceList) {
        if (attendanceList == null || attendanceList.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "⚠️ No attendance data provided"));
        }

        List<Attendance> attendanceRecords = new ArrayList<>();

        for (Map<String, Object> record : attendanceList) {
            try {
                Long studentId = Long.valueOf(record.get("studentId").toString());
                String status = record.get("status").toString();

                User student = userService.getUserById(studentId);
                if (student == null) continue;

                Attendance attendance = new Attendance();
                attendance.setStudent(student);
                attendance.setDate(LocalDate.now());
                attendance.setStatus(status);

                attendanceRecords.add(attendance);

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid attendance data format"));
            }
        }

        // ✅ Save all attendance
        attendanceService.markAttendance(attendanceRecords);

        // ✅ Always return valid JSON
        Map<String, Object> response = new HashMap<>();
        response.put("message", "✅ Attendance marked successfully");
        response.put("count", attendanceRecords.size());
        response.put("date", LocalDate.now().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ Get today’s attendance records
     */
    @GetMapping("/attendance/today")
    public ResponseEntity<?> getTodayAttendance() {
        List<Attendance> todayRecords = attendanceService.getTodayAttendance();
        return ResponseEntity.ok(todayRecords);
    }

    /**
     * ✅ Get attendance for a specific date (format: yyyy-MM-dd)
     */
    @GetMapping("/attendance/{date}")
    public ResponseEntity<?> getAttendanceByDate(@PathVariable String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            List<Attendance> records = attendanceService.getAttendanceByDate(parsedDate);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid date format. Use yyyy-MM-dd"));
        }
    }
}
