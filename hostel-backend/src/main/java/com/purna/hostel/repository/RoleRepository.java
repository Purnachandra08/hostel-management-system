package com.purna.hostel.repository;

import com.purna.hostel.entity.Role;
import com.purna.hostel.entity.RoleName;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Find role by enum name (ROLE_ADMIN, ROLE_STUDENT, ROLE_WARDEN)
    Optional<Role> findByName(RoleName name);

    // Check if role already exists
    boolean existsByName(RoleName name);
}