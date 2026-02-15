package com.purna.hostel.controller;

import com.purna.hostel.entity.LeaveRequest;
import com.purna.hostel.entity.User;
import com.purna.hostel.service.LeaveRequestService;
import com.purna.hostel.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leave")
@CrossOrigin(origins = "http://localhost:4200")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private UserRepository userRepository;

    // =====================================================
    // ✅ APPLY LEAVE (JWT BASED - NO userId in URL)
    // =====================================================
    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(
            @RequestBody LeaveRequest leaveRequest,
            Principal principal
    ) {
        try {
            // 🔐 Get logged-in username from JWT
            String username = principal.getName();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("❌ User not found"));

            LeaveRequest savedLeave = leaveRequestService.applyLeave(user, leaveRequest);

            return ResponseEntity.ok(Map.of(
                    "message", "✅ Leave applied successfully!",
                    "leaveId", savedLeave.getId(),
                    "user", user.getUsername(),
                    "status", savedLeave.getStatus()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error applying leave: " + e.getMessage()
            ));
        }
    }

    // =====================================================
    // ✅ GET MY LEAVES (JWT BASED)
    // =====================================================
    @GetMapping("/my-leaves")
    public ResponseEntity<?> getMyLeaves(Principal principal) {
        try {
            String username = principal.getName();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("❌ User not found"));

            List<LeaveRequest> leaves = leaveRequestService.getLeaveRequestsByUser(user.getId());

            return ResponseEntity.ok(leaves);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error fetching leaves: " + e.getMessage()
            ));
        }
    }

    // =====================================================
    // ✅ GET ALL LEAVES (WARDEN / ADMIN)
    // =====================================================
    @GetMapping("/all")
    public ResponseEntity<?> getAllLeaves() {
        try {
            List<LeaveRequest> allLeaves = leaveRequestService.getAllLeaveRequests();

            List<Map<String, Object>> formattedLeaves = allLeaves.stream()
                    .map(leave -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", leave.getId());
                        map.put("studentName",
                                leave.getUser() != null ? leave.getUser().getFullName() : "Unknown");
                        map.put("roomNumber",
                                leave.getUser() != null && leave.getUser().getRoom() != null
                                        ? leave.getUser().getRoom().getRoomNumber()
                                        : "N/A"
                        );
                        map.put("fromDate",
                                leave.getFromDate() != null ? leave.getFromDate().toString() : "N/A");
                        map.put("toDate",
                                leave.getToDate() != null ? leave.getToDate().toString() : "N/A");
                        map.put("reason", leave.getReason());
                        map.put("status", leave.getStatus());
                        map.put("type", leave.getType());
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(formattedLeaves);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error fetching all leaves: " + e.getMessage()
            ));
        }
    }

    // =====================================================
    // ✅ UPDATE LEAVE STATUS
    // =====================================================
    @PutMapping("/update-status/{leaveId}")
    public ResponseEntity<?> updateLeaveStatus(
            @PathVariable Long leaveId,
            @RequestParam String status
    ) {
        try {
            LeaveRequest updated =
                    leaveRequestService.updateLeaveStatus(leaveId, status.toUpperCase());

            return ResponseEntity.ok(Map.of(
                    "message", "✅ Leave status updated successfully",
                    "leaveId", leaveId,
                    "newStatus", updated.getStatus()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error updating status: " + e.getMessage()
            ));
        }
    }
}
