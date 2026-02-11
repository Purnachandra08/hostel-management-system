package com.purna.hostel.service;

import com.purna.hostel.entity.Role;
import com.purna.hostel.entity.RoleName;
import com.purna.hostel.entity.User;
import com.purna.hostel.repository.RoleRepository;
import com.purna.hostel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(@Lazy BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // BASIC CHECKS
    // =========================
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // =========================
    // FIND
    // =========================
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + id)
                );
    }

    // =========================
    // SAVE (GENERIC)
    // =========================
    public User save(User user) {
        return userRepository.save(user);
    }

    // =========================
    // STUDENT SELF REGISTER
    // =========================
    public User registerStudent(User user) {

        Role studentRole = roleRepository.findByName(RoleName.ROLE_STUDENT)
                .orElseThrow(() ->
                        new RuntimeException("ROLE_STUDENT not found")
                );

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(studentRole));

        return userRepository.save(user);
    }

    // =========================
    // ADMIN CREATES USER
    // =========================
    public User createUserByAdmin(User user, Set<RoleName> roleNames) {

        Set<Role> roles = new HashSet<>();

        for (RoleName roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() ->
                            new RuntimeException("Role not found: " + roleName)
                    );
            roles.add(role);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(roles);

        return userRepository.save(user);
    }

    // =========================
    // CRUD
    // =========================
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        user.getRoles().clear(); // FK safe
        userRepository.delete(user);
    }

    // =========================
    // ROLE BASED
    // =========================
    public List<User> getAllStudents() {
        return userRepository.findByRoleName(RoleName.ROLE_STUDENT);
    }

    public List<User> searchStudents(String keyword) {
        return userRepository.searchStudents(keyword, RoleName.ROLE_STUDENT);
    }
    
 // =========================
 // DELETE USER (ADMIN)
 // =========================
 public void deleteById(Long id) {

     if (!userRepository.existsById(id)) {
         throw new RuntimeException("User not found with id: " + id);
     }

     userRepository.deleteById(id);
 }

}
