package com.purna.hostel.repository;

import com.purna.hostel.entity.Role;
import com.purna.hostel.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
