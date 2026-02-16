package com.purna.hostel.controller;

import com.purna.hostel.entity.LeaveRequest;
import com.purna.hostel.entity.User;
import com.purna.hostel.repository.AttendanceRepository;
import com.purna.hostel.repository.ComplaintRepository;
import com.purna.hostel.repository.LeaveRequestRepository;
import com.purna.hostel.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warden")
@CrossOrigin(origins = "http://localhost:4200")
public class WardenController {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    // ====================================
    // ✅ DASHBOARD DATA
    // ====================================
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardData() {

        // Total students
        long totalStudents = userRepository.count();

        // Leaves pending
        long pendingLeaves = leaveRequestRepository.findAll()
                .stream()
                .filter(l -> "PENDING".equalsIgnoreCase(l.getStatus()))
                .count();

        // Total complaints
        long totalComplaints = complaintRepository.count();

        // Present & Absent Today
        LocalDate today = LocalDate.now();
        long presentToday = attendanceRepository.countByDateAndStatus(today, "PRESENT");
        long absentToday = attendanceRepository.countByDateAndStatus(today, "ABSENT");

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("wardenName", "Warden John"); // Replace with actual logged-in warden if needed
        dashboard.put("totalStudents", totalStudents);
        dashboard.put("pendingLeaves", pendingLeaves);
        dashboard.put("complaints", totalComplaints);
        dashboard.put("presentToday", presentToday);
        dashboard.put("absentToday", absentToday);

        return ResponseEntity.ok(dashboard);
    }

    // ====================================
    // ✅ GET ALL LEAVES
    // ====================================
    @GetMapping("/leaves")
    public ResponseEntity<?> getAllLeaves() {

        List<Map<String, Object>> leaves =
                leaveRequestRepository.findAll()
                        .stream()
                        .map(leave -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("id", leave.getId());
                            map.put("type", leave.getType());
                            map.put("fromDate", leave.getFromDate());
                            map.put("toDate", leave.getToDate());
                            map.put("reason", leave.getReason());
                            map.put("status", leave.getStatus());

                            if (leave.getUser() != null) {
                                map.put("studentName", leave.getUser().getFullName());
                                map.put("email", leave.getUser().getEmail());
                                map.put("roomNumber",
                                        leave.getUser().getRoom() != null
                                                ? leave.getUser().getRoom().getRoomNumber()
                                                : "N/A"
                                );
                            }
                            return map;
                        })
                        .collect(Collectors.toList());

        return ResponseEntity.ok(leaves);
    }

    // ====================================
    // ✅ APPROVE LEAVE
    // ====================================
    @PostMapping("/leaves/{id}/approve")
    public ResponseEntity<?> approveLeave(@PathVariable Long id) {
        LeaveRequest leave = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));
        leave.setStatus("APPROVED");
        leaveRequestRepository.save(leave);
        return ResponseEntity.ok(Map.of("message", "Leave approved successfully"));
    }

    // ====================================
    // ✅ REJECT LEAVE
    // ====================================
    @PostMapping("/leaves/{id}/reject")
    public ResponseEntity<?> rejectLeave(@PathVariable Long id) {
        LeaveRequest leave = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));
        leave.setStatus("REJECTED");
        leaveRequestRepository.save(leave);
        return ResponseEntity.ok(Map.of("message", "Leave rejected successfully"));
    }

    // ====================================
    // ✅ DELETE LEAVE
    // ====================================
    @DeleteMapping("/leaves/{id}")
    public ResponseEntity<?> deleteLeave(@PathVariable Long id) {
        leaveRequestRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Leave deleted successfully"));
    }
}
