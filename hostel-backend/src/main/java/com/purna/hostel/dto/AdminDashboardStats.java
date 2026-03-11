package com.purna.hostel.dto;

public class AdminDashboardStats {

    private long students;
    private long rooms;
    private long wardens;

    private long occupiedRooms;
    private long availableRooms;

    private long pendingComplaints;
    private long resolvedComplaints;

    // Default Constructor
    public AdminDashboardStats() {
    }

    // Parameterized Constructor
    public AdminDashboardStats(long students, long rooms, long wardens,
                               long occupiedRooms, long availableRooms,
                               long pendingComplaints, long resolvedComplaints) {

        this.students = students;
        this.rooms = rooms;
        this.wardens = wardens;
        this.occupiedRooms = occupiedRooms;
        this.availableRooms = availableRooms;
        this.pendingComplaints = pendingComplaints;
        this.resolvedComplaints = resolvedComplaints;
    }

    // Getters
    public long getStudents() {
        return students;
    }

    public long getRooms() {
        return rooms;
    }

    public long getWardens() {
        return wardens;
    }

    public long getOccupiedRooms() {
        return occupiedRooms;
    }

    public long getAvailableRooms() {
        return availableRooms;
    }

    public long getPendingComplaints() {
        return pendingComplaints;
    }

    public long getResolvedComplaints() {
        return resolvedComplaints;
    }

    // Setters
    public void setStudents(long students) {
        this.students = students;
    }

    public void setRooms(long rooms) {
        this.rooms = rooms;
    }

    public void setWardens(long wardens) {
        this.wardens = wardens;
    }

    public void setOccupiedRooms(long occupiedRooms) {
        this.occupiedRooms = occupiedRooms;
    }

    public void setAvailableRooms(long availableRooms) {
        this.availableRooms = availableRooms;
    }

    public void setPendingComplaints(long pendingComplaints) {
        this.pendingComplaints = pendingComplaints;
    }

    public void setResolvedComplaints(long resolvedComplaints) {
        this.resolvedComplaints = resolvedComplaints;
    }
}