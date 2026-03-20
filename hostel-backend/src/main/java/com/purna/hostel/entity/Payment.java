package com.purna.hostel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 RELATION (BETTER THAN JUST ID)
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private Long userId;

    // 🎯 ACADEMIC YEAR
    private String academicYear;

    private double roomFee;
    private double messFee;
    private double totalFee;

    private String paymentStatus; // PAID / PENDING

    private LocalDateTime paymentDate;

    // ======================
    // GETTERS & SETTERS
    // ======================

    public Long getId() { return id; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public double getRoomFee() { return roomFee; }
    public void setRoomFee(double roomFee) { this.roomFee = roomFee; }

    public double getMessFee() { return messFee; }
    public void setMessFee(double messFee) { this.messFee = messFee; }

    public double getTotalFee() { return totalFee; }
    public void setTotalFee(double totalFee) { this.totalFee = totalFee; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
}