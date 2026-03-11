package com.purna.hostel.service;

import com.purna.hostel.dto.AdminDashboardStats;
import com.purna.hostel.entity.RoleName;
import com.purna.hostel.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminAnalyticsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public AdminDashboardStats getAnalytics() {

        AdminDashboardStats stats = new AdminDashboardStats();

        stats.setStudents(
                userRepository.findByRoleName(RoleName.ROLE_STUDENT).size()
        );

        stats.setWardens(
                userRepository.findByRoleName(RoleName.ROLE_WARDEN).size()
        );

        stats.setRooms(roomRepository.count());

        long occupiedRooms = bookingRepository.countByStatusIgnoreCase("ACTIVE");

        stats.setOccupiedRooms(occupiedRooms);

        stats.setAvailableRooms(
                roomRepository.count() - occupiedRooms
        );

        stats.setPendingComplaints(
                complaintRepository.countByStatusIgnoreCase("PENDING")
        );

        stats.setResolvedComplaints(
                complaintRepository.countByStatusIgnoreCase("RESOLVED")
        );

        return stats;
    }
}