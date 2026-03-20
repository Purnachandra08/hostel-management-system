package com.purna.hostel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 USER RELATION
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 🔗 ROOM RELATION
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // 🎯 ACADEMIC YEAR (IMPORTANT)
    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    // ACTIVE / CANCELLED
    @Column(nullable = false)
    private String status = "ACTIVE";

    // TRUE = ACTIVE BOOKING
    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // ======================
    // GETTERS & SETTERS
    // ======================

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}