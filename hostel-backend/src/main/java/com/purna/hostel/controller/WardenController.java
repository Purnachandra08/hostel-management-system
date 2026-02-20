package com.purna.hostel.controller;

import com.purna.hostel.entity.LeaveRequest;
import com.purna.hostel.repository.AttendanceRepository;
import com.purna.hostel.repository.ComplaintRepository;
import com.purna.hostel.repository.LeaveRequestRepository;
import com.purna.hostel.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.file.*;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.Paragraph;

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

        long totalStudents = userRepository.count();
        long pendingLeaves = leaveRequestRepository.countByStatus("PENDING");
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
    // ✅ APPROVE LEAVE (Generate PDF)
    // ====================================
    @PostMapping("/leaves/{id}/approve")
    public ResponseEntity<?> approveLeave(@PathVariable Long id) {

        LeaveRequest leave = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        try {

            leave.setStatus("APPROVED");
            leave.setApprovedAt(LocalDateTime.now());

            // Create folder if not exists
            String folder = "gatepasses/";
            Files.createDirectories(Paths.get(folder));

            String fileName = "gatepass_" + leave.getId() + ".pdf";
            String fullPath = folder + fileName;

            // Generate PDF
            PdfWriter writer = new PdfWriter(fullPath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("HOSTEL GATE PASS"));
            document.add(new Paragraph("-----------------------------------"));
            document.add(new Paragraph("Student Name: " + leave.getUser().getFullName()));
            document.add(new Paragraph("Room Number: " +
                    (leave.getUser().getRoom() != null
                            ? leave.getUser().getRoom().getRoomNumber()
                            : "N/A")));
            document.add(new Paragraph("Leave Type: " + leave.getType()));
            document.add(new Paragraph("From Date: " + leave.getFromDate()));
            document.add(new Paragraph("To Date: " + leave.getToDate()));
            document.add(new Paragraph("Reason: " + leave.getReason()));
            document.add(new Paragraph("Approved On: " + leave.getApprovedAt()));
            document.add(new Paragraph("-----------------------------------"));
            document.add(new Paragraph("Signature: Warden"));

            document.close();

            // Save path in DB
            leave.setGatePassPath(fullPath);

            leaveRequestRepository.save(leave);

            return ResponseEntity.ok(Map.of("message", "Leave approved & Gate Pass generated"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error generating gate pass");
        }
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

    // ====================================
    // ✅ DOWNLOAD GATE PASS
    // ====================================
    @GetMapping("/leaves/download/{id}")
    public ResponseEntity<?> downloadGatePass(@PathVariable Long id) throws Exception {

        LeaveRequest leave = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        if (!"APPROVED".equalsIgnoreCase(leave.getStatus())) {
            return ResponseEntity.badRequest().body("Leave not approved yet");
        }

        Path path = Paths.get(leave.getGatePassPath());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=" + path.getFileName())
                .body(resource);
    }
}