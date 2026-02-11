package com.purna.hostel.controller;

import com.purna.hostel.dto.AdminCreateUserRequest;
import com.purna.hostel.service.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    // ==============================
    // 👑 ADMIN → CREATE WARDEN / ADMIN
    // ==============================
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> createUserByAdmin(
            @RequestBody AdminCreateUserRequest request
    ) {
        return ResponseEntity.ok(
                adminUserService.createUser(request)
        );
    }
}
