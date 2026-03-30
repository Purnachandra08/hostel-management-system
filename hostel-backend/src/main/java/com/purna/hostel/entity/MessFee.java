package com.purna.hostel.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "mess_fee",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "month", "year"}))
public class MessFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String academicYear; // 2026-2027

    private int month; // 1–12
    private int year;  // 2026

    private double amount;

    private String status = "UNPAID"; // PAID / UNPAID

    // ======================
    // GETTERS & SETTERS
    // ======================

    public Long getId() { return id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}