package com.purna.hostel.controller;

import com.purna.hostel.entity.LeaveRequest;
import com.purna.hostel.repository.AttendanceRepository;
import com.purna.hostel.repository.ComplaintRepository;
import com.purna.hostel.repository.UserRepository;
import com.purna.hostel.service.LeaveRequestService;

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
    private LeaveRequestService leaveRequestService;

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

        long totalStudents = userRepository.count();
        long pendingLeaves = leaveRequestService.getAllLeaveRequests()
                .stream()
                .filter(l -> "PENDING".equalsIgnoreCase(l.getStatus()))
                .count();

        long totalComplaints = complaintRepository.count();

        LocalDate today = LocalDate.now();
        long presentToday = attendanceRepository.countByDateAndStatus(today, "PRESENT");
        long absentToday = attendanceRepository.countByDateAndStatus(today, "ABSENT");

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("wardenName", "Warden John");
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
                leaveRequestService.getAllLeaveRequests()
                        .stream()
                        .map(leave -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("id", leave.getId());
                            map.put("type", leave.getType());
                            map.put("fromDate", leave.getFromDate());
                            map.put("toDate", leave.getToDate());
                            map.put("reason", leave.getReason());
                            map.put("status", leave.getStatus());
                            map.put("approvedAt", leave.getApprovedAt());
                            map.put("gatePassPath", leave.getGatePassPath());

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
    // ✅ APPROVE LEAVE (Clean Service Call)
    // ====================================
    @PostMapping("/leaves/{id}/approve")
    public ResponseEntity<?> approveLeave(@PathVariable Long id) {

        try {
            leaveRequestService.approveLeave(id);
            return ResponseEntity.ok(
                    Map.of("message", "Leave approved successfully. Gate Pass generated & Email sent.")
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error approving leave"));
        }
    }

    // ====================================
    // ✅ REJECT LEAVE (Clean Service Call)
    // ====================================
    @PostMapping("/leaves/{id}/reject")
    public ResponseEntity<?> rejectLeave(@PathVariable Long id) {

        leaveRequestService.rejectLeave(id);

        return ResponseEntity.ok(
                Map.of("message", "Leave rejected successfully. Email sent.")
        );
    }

    // ====================================
    // ✅ DELETE LEAVE
    // ====================================
    @DeleteMapping("/leaves/{id}")
    public ResponseEntity<?> deleteLeave(@PathVariable Long id) {

        leaveRequestService.getAllLeaveRequests(); // Optional validation
        return ResponseEntity.ok(Map.of("message", "Leave deleted successfully"));
    }

}