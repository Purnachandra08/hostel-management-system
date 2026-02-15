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

    // =====================================================
    // ✅ WARDEN DASHBOARD SUMMARY
    // =====================================================
    @GetMapping("/dashboard")
    public ResponseEntity<?> getWardenSummary() {

        long totalStudents = userRepository.findAll().stream()
                .filter(u -> u.getRoles() != null &&
                        u.getRoles().stream()
                                .anyMatch(r -> r.getName() == RoleName.ROLE_STUDENT))
                .count();

        long pendingLeaves = leaveRequestRepository.countByStatus("PENDING");
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

    // =====================================================
    // ✅ GET ALL LEAVES
    // =====================================================
    @GetMapping("/leaves")
    public ResponseEntity<?> getAllLeaves() {
        try {
            List<Map<String, Object>> leaves = leaveRequestRepository.findAll()
                    .stream()
                    .map(this::convertLeaveToMap)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(leaves);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to load leave requests"));
        }
    }

    // =====================================================
    // ✅ GET PENDING LEAVES
    // =====================================================
    @GetMapping("/leaves/pending")
    public ResponseEntity<?> getPendingLeaves() {
        List<Map<String, Object>> pendingLeaves = leaveRequestRepository.findAll()
                .stream()
                .filter(lr -> "PENDING".equalsIgnoreCase(lr.getStatus()))
                .map(this::convertLeaveToMap)
                .collect(Collectors.toList());

        return ResponseEntity.ok(pendingLeaves);
    }

    // =====================================================
    // ✅ APPROVE LEAVE
    // =====================================================
    @PostMapping("/leaves/{id}/approve")
    public ResponseEntity<?> approveLeave(@PathVariable Long id) {
        return updateLeaveStatus(id, "APPROVED");
    }

    // =====================================================
    // ✅ REJECT LEAVE
    // =====================================================
    @PostMapping("/leaves/{id}/reject")
    public ResponseEntity<?> rejectLeave(@PathVariable Long id) {
        return updateLeaveStatus(id, "REJECTED");
    }

    // =====================================================
    // ✅ DELETE LEAVE (IMPORTANT – Angular uses this)
    // =====================================================
    @DeleteMapping("/leaves/{id}")
    public ResponseEntity<?> deleteLeave(@PathVariable Long id) {
        if (!leaveRequestRepository.existsById(id)) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Leave not found"));
        }

        leaveRequestRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Leave deleted successfully"));
    }

    // =====================================================
    // ✅ UPDATE STATUS (COMMON METHOD)
    // =====================================================
    private ResponseEntity<?> updateLeaveStatus(Long id, String status) {

        Optional<LeaveRequest> leaveOpt = leaveRequestRepository.findById(id);

        if (leaveOpt.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Leave request not found"));
        }

        LeaveRequest leave = leaveOpt.get();
        leave.setStatus(status);
        leaveRequestRepository.save(leave);

        return ResponseEntity.ok(Map.of(
                "message", "Leave status updated successfully",
                "status", status
        ));
    }

    // =====================================================
    // ✅ CONVERT LEAVE TO MAP (DTO)
    // =====================================================
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
            map.put("studentName",
                    user.getFullName() != null
                            ? user.getFullName()
                            : user.getUsername());

            map.put("email", user.getEmail());

            map.put("roomNumber",
                    user.getRoom() != null
                            ? user.getRoom().getRoomNumber()
                            : "N/A");
        } else {
            map.put("studentName", "Unknown");
            map.put("roomNumber", "N/A");
        }

        return map;
    }

    // =====================================================
    // ✅ GET ALL COMPLAINTS
    // =====================================================
    @GetMapping("/complaints")
    public ResponseEntity<?> getAllComplaints() {
        return ResponseEntity.ok(complaintRepository.findAll());
    }

    // =====================================================
    // ✅ RESOLVE COMPLAINT
    // =====================================================
    @PostMapping("/complaints/{id}/resolve")
    public ResponseEntity<?> resolveComplaint(@PathVariable Long id) {

        Optional<Complaint> complaintOpt = complaintRepository.findById(id);

        if (complaintOpt.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Complaint not found"));
        }

        Complaint complaint = complaintOpt.get();
        complaint.setStatus("RESOLVED");
        complaintRepository.save(complaint);

        return ResponseEntity.ok(
                Map.of("message", "Complaint marked as resolved")
        );
    }
}
