package com.purna.hostel.controller;

import com.purna.hostel.entity.Complaint;
import com.purna.hostel.entity.User;
import com.purna.hostel.service.ComplaintService;
import com.purna.hostel.service.UserService;
import com.purna.hostel.service.email.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "http://localhost:4200")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    // =========================
    // ✅ Submit complaint by student
    // =========================
    @PostMapping("/submit")
    public ResponseEntity<?> submitComplaint(@RequestBody Map<String, Object> request) {

        Long userId = Long.valueOf(request.get("userId").toString());
        String subject = request.get("subject").toString();
        String description = request.get("description").toString();

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        Complaint complaint = new Complaint();
        complaint.setUser(user);
        complaint.setSubject(subject);
        complaint.setDescription(description);
        complaint.setStatus("PENDING");

        Complaint saved = complaintService.saveComplaint(complaint);

        // 📧 Send email after submit
        emailService.sendEmail(
                user.getEmail(),
                "Complaint Submitted Successfully",
                "Dear " + user.getFullName() +
                        ",\n\nYour complaint has been submitted successfully.\n\n" +
                        "Subject: " + subject +
                        "\nStatus: PENDING\n\nThank you."
        );

        return ResponseEntity.ok(saved);
    }

    // =========================
    // ✅ Get complaints by student
    // =========================
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getComplaintsByUser(@PathVariable Long userId) {
        List<Complaint> complaints = complaintService.getComplaintsByUser(userId);
        return ResponseEntity.ok(complaints);
    }

    // =========================
    // ✅ Get all complaints (Warden)
    // =========================
    @GetMapping("/all")
    public ResponseEntity<?> getAllComplaints() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    // =========================
    // ✅ Update complaint status
    // =========================
    @PutMapping("/update-status/{id}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Complaint complaint = complaintService.updateComplaintStatus(id, status);

        if (complaint == null) {
            return ResponseEntity.badRequest().body("Complaint not found");
        }

        // 📧 Send status update email
        emailService.sendEmail(
                complaint.getUser().getEmail(),
                "Complaint Status Updated",
                "Dear " + complaint.getUser().getFullName() +
                        ",\n\nYour complaint status has been updated.\n\n" +
                        "Subject: " + complaint.getSubject() +
                        "\nNew Status: " + status +
                        "\n\nThank you."
        );

        return ResponseEntity.ok(complaint);
    }

    // =========================
    // ✅ Delete complaint (Warden)
    // =========================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteComplaint(@PathVariable Long id) {

        Complaint complaint = complaintService.getComplaintById(id);
        if (complaint == null) {
            return ResponseEntity.badRequest().body("Complaint not found");
        }

        complaintService.deleteComplaint(id);

        // 📧 Send email after delete
        emailService.sendEmail(
                complaint.getUser().getEmail(),
                "Complaint Removed",
                "Dear " + complaint.getUser().getFullName() +
                        ",\n\nYour complaint has been removed by the administration.\n\n" +
                        "Subject: " + complaint.getSubject()
        );

        return ResponseEntity.ok("Complaint deleted successfully");
    }
}