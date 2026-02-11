package com.purna.hostel.service;

import com.purna.hostel.dto.AdminCreateUserRequest;
import com.purna.hostel.entity.Role;
import com.purna.hostel.entity.RoleName;
import com.purna.hostel.entity.User;
import com.purna.hostel.repository.RoleRepository;
import com.purna.hostel.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==============================
    // 👑 CREATE ADMIN / WARDEN
    // ==============================
    public String createUser(AdminCreateUserRequest request) {

        // ❌ Admin cannot create students
        if (request.getRole() == RoleName.ROLE_STUDENT) {
            throw new RuntimeException("Admin cannot create student accounts");
        }

        // ✅ Fetch role entity
        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // ✅ Create user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(role));

        userRepository.save(user);

        return request.getRole().name() + " created successfully";
    }
}
