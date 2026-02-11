package com.purna.hostel.controller;

import com.purna.hostel.entity.User;
import com.purna.hostel.entity.Role;
import com.purna.hostel.entity.RoleName;
import com.purna.hostel.service.UserService;
import com.purna.hostel.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/users/students")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserService userService;

    // =========================
    // 👑 ADMIN: GET ALL STUDENTS
    // =========================
    @GetMapping
    public List<User> getAllStudents() {
        return userService.getAllStudents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getStudentById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createStudent(@RequestBody User student) {
        User created = userService.createUserByAdmin(
                student,
                Set.of(RoleName.ROLE_STUDENT)
        );
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateStudent(
            @PathVariable Long id,
            @RequestBody User updated
    ) {
        User existing = userService.getUserById(id);

        existing.setFullName(updated.getFullName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setUsername(updated.getUsername());
        existing.setRoom(updated.getRoom());

        return ResponseEntity.ok(userService.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Student deleted"));
    }
}
