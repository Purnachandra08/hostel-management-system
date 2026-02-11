package com.purna.hostel.service;

import com.purna.hostel.entity.LeaveRequest;
import com.purna.hostel.entity.User;
import com.purna.hostel.repository.LeaveRequestRepository;
import com.purna.hostel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ Apply leave
    @Transactional
    public LeaveRequest applyLeave(User user, LeaveRequest leaveRequest) {
        leaveRequest.setUser(user);
        leaveRequest.setStatus("PENDING");
        System.out.println("🟢 Applying leave for user: " + user.getUsername());
        return leaveRequestRepository.save(leaveRequest);
    }

    // ✅ Get all leaves for a specific user
    public List<LeaveRequest> getLeaveRequestsByUser(Long userId) {
        return leaveRequestRepository.findByUserId(userId);
    }

    // ✅ Get all leaves (for admin)
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    // ✅ Update leave status
    @Transactional
    public LeaveRequest updateLeaveStatus(Long leaveId, String status) {
        LeaveRequest leave = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found with ID: " + leaveId));
        leave.setStatus(status);
        return leaveRequestRepository.save(leave);
    }
}
