package com.purna.hostel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "room_number", unique = true)
    private String roomNumber;

    @Column(nullable = false)
    private String type; // SINGLE, DOUBLE, TRIPLE

    @Column(nullable = false)
    private int capacity;

    @Column(name = "price_per_month", nullable = false)
    private double pricePerMonth;

    @Column(nullable = false)
    private String status = "AVAILABLE";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // ======================
    // USERS IN ROOM
    // ======================
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    public Room() {}

    public Room(String roomNumber, String type, int capacity, double pricePerMonth) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.capacity = capacity;
        this.pricePerMonth = pricePerMonth;
    }

    // ======================
    // GETTERS & SETTERS
    // ======================
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }

    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public int getCapacity() { return capacity; }

    public void setCapacity(int capacity) { this.capacity = capacity; }

    public double getPricePerMonth() { return pricePerMonth; }

    public void setPricePerMonth(double pricePerMonth) { this.pricePerMonth = pricePerMonth; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<User> getUsers() { return users; }

    public void setUsers(Set<User> users) { this.users = users; }
}