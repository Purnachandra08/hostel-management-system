package com.purna.hostel.controller;

import com.purna.hostel.dto.AdminCreateUserRequest;
import com.purna.hostel.entity.User;
import com.purna.hostel.service.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

        User user = adminUserService.createUser(request);

        return ResponseEntity.ok(
                Map.of(
                        "message", "User created successfully",
                        "username", user.getUsername(),
                        "role", user.getRoles().iterator().next().getName()
                )
        );
    }

    // ==============================
    // 📋 GET ALL WARDENS
    // ==============================
    @GetMapping("/wardens")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getAllWardens() {
        return ResponseEntity.ok(adminUserService.getAllWardens());
    }

    // ==============================
    // 🗑 DELETE WARDEN
    // ==============================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.ok(
                Map.of("message", "User deleted successfully")
        );
    }
}
