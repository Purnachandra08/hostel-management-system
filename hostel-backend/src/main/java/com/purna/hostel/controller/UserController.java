package com.purna.hostel.controller;

import com.purna.hostel.entity.User;
import com.purna.hostel.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserService userService;

    // =========================================
    // ✅ GET ALL USERS (ADMIN ONLY)
    // This fixes 404 error for /api/users
    // =========================================
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    // =========================================
    // 👑 GET ALL STUDENTS (WARDEN + ADMIN)
    // =========================================
    @GetMapping("/students")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_WARDEN')")
    public List<User> getAllStudents() {
        return userService.getAllStudents();
    }

    // =========================================
    // ✅ GET STUDENT BY ID
    // =========================================
    @GetMapping("/students/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_WARDEN')")
    public ResponseEntity<User> getStudentById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
