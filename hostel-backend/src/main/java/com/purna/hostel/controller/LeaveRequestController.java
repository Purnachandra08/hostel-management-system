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
}
