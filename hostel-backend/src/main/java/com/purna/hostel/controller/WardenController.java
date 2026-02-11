package com.purna.hostel.controller;

import com.purna.hostel.entity.LeaveRequest;
import com.purna.hostel.entity.Complaint;
import com.purna.hostel.entity.RoleName;
import com.purna.hostel.entity.User;
import com.purna.hostel.repository.LeaveRequestRepository;
import com.purna.hostel.repository.ComplaintRepository;
import com.purna.hostel.repository.UserRepository;
import com.purna.hostel.repository.AttendanceRepository;

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
    private UserRepository userRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    // ✅ Dashboard summary for Warden
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getWardenSummary() {
        long totalStudents = userRepository.findAll().stream()
                .filter(u -> u.getRoles() != null &&
                        u.getRoles().stream()
                        .anyMatch(r -> r.getName() == RoleName.ROLE_STUDENT))
                .count();

        long pendingLeaves = leaveRequestRepository.findAll().stream()
                .filter(lr -> "PENDING".equalsIgnoreCase(lr.getStatus()))
                .count();

        long totalComplaints = complaintRepository.count();
        long presentToday = attendanceRepository.countByDateAndStatus(LocalDate.now(), "PRESENT");
        long absentToday = attendanceRepository.countByDateAndStatus(LocalDate.now(), "ABSENT");

        Map<String, Object> summary = new HashMap<>();
        summary.put("wardenName", "Warden");
        summary.put("totalStudents", totalStudents);
        summary.put("pendingLeaves", pendingLeaves);
        summary.put("complaints", totalComplaints);
        summary.put("presentToday", presentToday);
        summary.put("absentToday", absentToday);

        return ResponseEntity.ok(summary);
    }

    // ✅ Get all leave requests (with student details)
    @GetMapping("/leaves")
    public ResponseEntity<List<Map<String, Object>>> getAllLeaves() {
        List<Map<String, Object>> leavesWithDetails = leaveRequestRepository.findAll()
                .stream()
                .map(this::convertLeaveToMap)
                .collect(Collectors.toList());

        return ResponseEntity.ok(leavesWithDetails);
    }

    // ✅ Get all pending leave requests (with student details)
    @GetMapping("/leaves/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingLeaves() {
        List<Map<String, Object>> pendingLeaves = leaveRequestRepository.findAll().stream()
                .filter(lr -> "PENDING".equalsIgnoreCase(lr.getStatus()))
                .map(this::convertLeaveToMap)
                .collect(Collectors.toList());

        return ResponseEntity.ok(pendingLeaves);
    }

    // ✅ Approve Leave
    @PostMapping("/leaves/{id}/approve")
    public ResponseEntity<Map<String, String>> approveLeave(@PathVariable Long id) {
        return updateLeaveStatus(id, "APPROVED", "✅ Leave approved successfully");
    }

    // ✅ Reject Leave
    @PostMapping("/leaves/{id}/reject")
    public ResponseEntity<Map<String, String>> rejectLeave(@PathVariable Long id) {
        return updateLeaveStatus(id, "REJECTED", "❌ Leave rejected successfully");
    }

    // ✅ JSON response for leave updates
    private ResponseEntity<Map<String, String>> updateLeaveStatus(Long id, String status, String message) {
        Optional<LeaveRequest> leaveOpt = leaveRequestRepository.findById(id);
        if (leaveOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "⚠️ Leave request not found"));
        }

        LeaveRequest leave = leaveOpt.get();
        leave.setStatus(status);
        leaveRequestRepository.save(leave);

        return ResponseEntity.ok(Map.of("message", message));
    }

    // ✅ Convert LeaveRequest to Map (DTO for frontend)
    private Map<String, Object> convertLeaveToMap(LeaveRequest leave) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", leave.getId());
        map.put("type", leave.getType());
        map.put("fromDate", leave.getFromDate());
        map.put("toDate", leave.getToDate());
        map.put("reason", leave.getReason());
        map.put("status", leave.getStatus());
        map.put("appliedAt", leave.getAppliedAt());

        User user = leave.getUser();
        if (user != null) {
            map.put("studentName", user.getFullName() != null ? user.getFullName() : user.getUsername());
            map.put("email", user.getEmail());
            map.put("roomNumber", user.getRoom() != null ? user.getRoom().getRoomNumber() : "N/A");
        } else {
            map.put("studentName", "Unknown");
            map.put("roomNumber", "N/A");
        }

        return map;
    }

    // ✅ Get all complaints
    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        List<Complaint> complaints = complaintRepository.findAll();
        return ResponseEntity.ok(complaints);
    }

    // ✅ Resolve complaint
    @PostMapping("/complaints/{id}/resolve")
    public ResponseEntity<Map<String, String>> resolveComplaint(@PathVariable Long id) {
        Optional<Complaint> complaintOpt = complaintRepository.findById(id);
        if (complaintOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "⚠️ Complaint not found"));
        }

        Complaint complaint = complaintOpt.get();
        complaint.setStatus("RESOLVED");
        complaintRepository.save(complaint);

        return ResponseEntity.ok(Map.of("message", "🟢 Complaint marked as resolved successfully"));
    }
}
