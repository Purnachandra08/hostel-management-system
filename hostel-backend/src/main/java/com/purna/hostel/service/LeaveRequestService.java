package com.purna.hostel.service;

import com.purna.hostel.entity.LeaveRequest;
import com.purna.hostel.entity.User;
import com.purna.hostel.repository.LeaveRequestRepository;
import com.purna.hostel.service.email.EmailService;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.Paragraph;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmailService emailService;

    // ====================================
    // ✅ APPLY LEAVE
    // ====================================
    @Transactional
    public LeaveRequest applyLeave(User user, LeaveRequest leaveRequest) {

        leaveRequest.setUser(user);
        leaveRequest.setStatus("PENDING");

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);

        emailService.sendLeaveAppliedEmail(
                user.getEmail(),
                user.getUsername()
        );

        return saved;
    }

    // ====================================
    // ✅ GET METHODS
    // ====================================
    public List<LeaveRequest> getLeaveRequestsByUser(Long userId) {
        return leaveRequestRepository.findByUserId(userId);
    }

    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    public LeaveRequest getLeaveById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));
    }

    // ====================================
    // ❌ REJECT LEAVE
    // ====================================
    @Transactional
    public LeaveRequest rejectLeave(Long leaveId) {

        LeaveRequest leave = getLeaveById(leaveId);

        if ("APPROVED".equals(leave.getStatus())) {
            throw new RuntimeException("Approved leave cannot be rejected");
        }

        if ("REJECTED".equals(leave.getStatus())) {
            throw new RuntimeException("Leave already rejected");
        }

        leave.setStatus("REJECTED");

        LeaveRequest updated = leaveRequestRepository.save(leave);

        emailService.sendLeaveStatusEmail(
                leave.getUser().getEmail(),
                leave.getUser().getUsername(),
                "REJECTED"
        );

        return updated;
    }

    // ====================================
    // ✅ APPROVE LEAVE (FULL SAFE VERSION)
    // ====================================
    @Transactional
    public LeaveRequest approveLeave(Long leaveId)
            throws IOException, MessagingException {

        LeaveRequest leave = getLeaveById(leaveId);

        if ("APPROVED".equals(leave.getStatus())) {
            throw new RuntimeException("Leave already approved");
        }

        if ("REJECTED".equals(leave.getStatus())) {
            throw new RuntimeException("Rejected leave cannot be approved");
        }

        leave.setStatus("APPROVED");
        leave.setApprovedAt(LocalDateTime.now());

        // Use absolute project directory path
        String folder = System.getProperty("user.dir") + "/gatepasses/";
        Files.createDirectories(Paths.get(folder));

        String fileName = "gatepass_" + leave.getId() + ".pdf";
        String fullPath = folder + fileName;

        // Generate PDF
        try (PdfWriter writer = new PdfWriter(fullPath);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

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

        }

        leave.setGatePassPath(fullPath);

        LeaveRequest updated = leaveRequestRepository.save(leave);

        // Send email with attachment
        byte[] pdfBytes = Files.readAllBytes(Paths.get(fullPath));

        emailService.sendGatePassEmail(
                leave.getUser().getEmail(),
                leave.getUser().getUsername(),
                pdfBytes
        );

        return updated;
    }
}