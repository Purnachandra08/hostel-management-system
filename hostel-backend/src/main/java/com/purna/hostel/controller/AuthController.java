package com.purna.hostel.controller;

import com.purna.hostel.dto.RegisterRequestDTO;
import com.purna.hostel.dto.UserDTO;
import com.purna.hostel.entity.Role;
import com.purna.hostel.entity.RoleName;
import com.purna.hostel.entity.User;
import com.purna.hostel.repository.RoleRepository;
import com.purna.hostel.security.JwtUtils;
import com.purna.hostel.security.otp.OtpService;
import com.purna.hostel.service.UserService;
import com.purna.hostel.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // =========================
    // ✅ REGISTER (STUDENT ONLY)
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {

        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }

        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
        }

        Role studentRole = roleRepository.findByName(RoleName.ROLE_STUDENT)
                .orElseThrow(() -> new RuntimeException("ROLE_STUDENT not found"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(studentRole));

        User savedUser = userService.save(user);

        UserDTO dto = new UserDTO();
        dto.setId(savedUser.getId());
        dto.setUsername(savedUser.getUsername());
        dto.setEmail(savedUser.getEmail());
        dto.setFullName(savedUser.getFullName());
        dto.setPhone(savedUser.getPhone());
        dto.setRoles(
                savedUser.getRoles()
                        .stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet())
        );

        return ResponseEntity.ok(dto);
    }

    // =========================
    // 🔐 LOGIN → SEND OTP
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username and password are required"));
        }

        User user = userService.findByUsername(username)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "User not found"));
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid username or password"));
        }

        String email = user.getEmail();
        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, otp);

        return ResponseEntity.ok(Map.of(
                "message", "OTP sent successfully",
                "email", email
        ));
    }



    // =========================
    // 🔑 VERIFY OTP → JWT
    // =========================
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String otp = request.get("otp");

        if (!otpService.validateOtp(email, otp)) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid or expired OTP"));
        }

        User user = userService.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "User not found"));
        }

        RoleName roleName = user.getRoles()
                .iterator()
                .next()
                .getName();

        String token = jwtUtils.generateToken(
                user.getUsername(),
                roleName.name()
        );

        // ✅ Return FULL USER DATA including ID
        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "fullName", user.getFullName(),
                        "role", roleName.name()
                )
        ));
    }

}
