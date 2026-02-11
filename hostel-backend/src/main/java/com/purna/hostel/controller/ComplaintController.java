package com.purna.hostel.controller;

import com.purna.hostel.entity.Complaint;
import com.purna.hostel.entity.User;
import com.purna.hostel.service.ComplaintService;
import com.purna.hostel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "http://localhost:4200")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private UserService userService;

    // =========================
    // ✅ Submit complaint by student
    // =========================
    @PostMapping("/submit")
    public ResponseEntity<?> submitComplaint(@RequestBody Map<String, Object> request) {

        // Extract userId and other complaint details from request
        Long userId = ((Number) request.get("userId")).longValue();
        String subject = (String) request.get("subject");
        String description = (String) request.get("description");

        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID is required"));
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }

        Complaint complaint = new Complaint();
        complaint.setUser(user);
        complaint.setSubject(subject);
        complaint.setDescription(description);
        complaint.setStatus("PENDING"); // default status

        Complaint saved = complaintService.submitComplaint(user, complaint);
        return ResponseEntity.ok(saved);
    }

    // =========================
    // ✅ Get complaints by a specific user (Student View)
    // =========================
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getComplaintsByUser(@PathVariable Long userId) {
        List<Complaint> complaints = complaintService.getComplaintsByUser(userId);

        List<Map<String, Object>> result = complaints.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("studentName", c.getUser() != null ? c.getUser().getFullName() : "Unknown");
            map.put("subject", c.getSubject());
            map.put("complaint", c.getDescription());
            map.put("status", c.getStatus());
            map.put("date", c.getCreatedAt());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // =========================
    // ✅ Get all complaints (Warden/Admin View)
    // =========================
    @GetMapping("/all")
    public ResponseEntity<?> getAllComplaints() {
        List<Complaint> complaints = complaintService.getAllComplaints();

        List<Map<String, Object>> result = complaints.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("studentName", c.getUser() != null ? c.getUser().getFullName() : "Unknown");
            map.put("subject", c.getSubject());
            map.put("complaint", c.getDescription());
            map.put("status", c.getStatus());
            map.put("date", c.getCreatedAt());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // =========================
    // ✅ Update complaint status (Resolve)
    // =========================
    @PutMapping("/update-status/{complaintId}")
    public ResponseEntity<?> updateComplaintStatus(
            @PathVariable Long complaintId,
            @RequestParam String status
    ) {
        Complaint updated = complaintService.updateComplaintStatus(complaintId, status);
        if (updated == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Complaint not found"));
        }
        return ResponseEntity.ok(updated);
    }
}
