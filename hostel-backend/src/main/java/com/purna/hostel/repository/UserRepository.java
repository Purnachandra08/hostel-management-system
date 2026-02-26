package com.purna.hostel.repository;

import com.purna.hostel.entity.User;
import com.purna.hostel.entity.RoleName;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    // =========================
    // FIND USERS BY ROLE
    // =========================
    @Query("""
        SELECT u FROM User u
        JOIN u.roles r
        WHERE r.name = :roleName
    """)
    List<User> findByRoleName(@Param("roleName") RoleName roleName);

    // =========================
    // SEARCH STUDENTS
    // =========================
    @Query("""
        SELECT u FROM User u
        JOIN u.roles r
        WHERE r.name = :studentRole
        AND (
            LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)
    List<User> searchStudents(
            @Param("keyword") String keyword,
            @Param("studentRole") RoleName studentRole
    );
}