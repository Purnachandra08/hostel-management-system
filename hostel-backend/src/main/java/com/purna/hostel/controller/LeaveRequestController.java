package com.purna.hostel.controller;

import com.purna.hostel.entity.LeaveRequest;
import com.purna.hostel.entity.User;
import com.purna.hostel.service.LeaveRequestService;
import com.purna.hostel.repository.UserRepository;
import com.purna.hostel.repository.LeaveRequestRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/leave")
@CrossOrigin(origins = "http://localhost:4200")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    // ====================================
    // ✅ APPLY LEAVE
    // ====================================
    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(
            @RequestBody LeaveRequest leaveRequest,
            Principal principal
    ) {

        String username = principal.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LeaveRequest saved =
                leaveRequestService.applyLeave(user, leaveRequest);

        return ResponseEntity.ok(saved);
    }

    // ====================================
    // ✅ GET MY LEAVES
    // ====================================
    @GetMapping("/my-leaves")
    public ResponseEntity<?> getMyLeaves(Principal principal) {

        String username = principal.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(
                leaveRequestService.getLeaveRequestsByUser(user.getId())
        );
    }

    // ====================================
    // ✅ DOWNLOAD GATE PASS (SECURE)
    // ====================================
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadGatePass(
            @PathVariable Long id,
            Principal principal
    ) throws IOException {

        String username = principal.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LeaveRequest leave = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        // 🔐 SECURITY CHECK
        if (!leave.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        if (!"APPROVED".equals(leave.getStatus())) {
            throw new RuntimeException("Gate pass not available");
        }

        Path path = Paths.get(leave.getGatePassPath());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"GatePass_" + id + ".pdf\"")
                .body(resource);
    }
}