package com.purna.hostel.controller;

import com.purna.hostel.entity.LeaveRequest;
import com.purna.hostel.entity.User;
import com.purna.hostel.service.LeaveRequestService;
import com.purna.hostel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // ✅ Apply leave
    @PostMapping("/apply/{userId}")
    public ResponseEntity<?> applyLeave(@PathVariable Long userId, @RequestBody LeaveRequest leaveRequest) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("❌ User not found with ID: " + userId));

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

    // ✅ Get all leaves of a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserLeaves(@PathVariable Long userId) {
        try {
            List<LeaveRequest> leaves = leaveRequestService.getLeaveRequestsByUser(userId);
            return ResponseEntity.ok(leaves);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error fetching leaves: " + e.getMessage()
            ));
        }
    }

    // ✅ Get all leaves (for warden approval panel)
    @GetMapping("/all")
    public ResponseEntity<?> getAllLeaves() {
        try {
            List<LeaveRequest> allLeaves = leaveRequestService.getAllLeaveRequests();

            // 🔹 Convert entity data into simplified JSON for frontend
            List<Map<String, Object>> formattedLeaves = allLeaves.stream()
                    .map(leave -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", leave.getId());
                        map.put("studentName", leave.getUser() != null ? leave.getUser().getFullName() : "Unknown");
                        map.put("roomNumber",
                                leave.getUser() != null && leave.getUser().getRoom() != null
                                        ? leave.getUser().getRoom().getRoomNumber()
                                        : "N/A"
                        );
                        // ✅ FIX: Ensure correct date mapping
                        map.put("fromDate", leave.getFromDate() != null ? leave.getFromDate().toString() : "N/A");
                        map.put("toDate", leave.getToDate() != null ? leave.getToDate().toString() : "N/A");
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

    // ✅ Update leave status (approve / reject)
    @PutMapping("/update-status/{leaveId}")
    public ResponseEntity<?> updateLeaveStatus(
            @PathVariable Long leaveId,
            @RequestParam String status
    ) {
        try {
            LeaveRequest updated = leaveRequestService.updateLeaveStatus(leaveId, status.toUpperCase());
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
