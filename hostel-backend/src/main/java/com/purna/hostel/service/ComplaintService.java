package com.purna.hostel.service;

import com.purna.hostel.entity.Complaint;
import com.purna.hostel.entity.User;
import com.purna.hostel.repository.ComplaintRepository;
import com.purna.hostel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ Submit new complaint
    public Complaint submitComplaint(User user, Complaint complaint) {
        complaint.setUser(user);
        complaint.setUserId(user.getId()); // ensure consistency
        complaint.setStatus("PENDING");
        return complaintRepository.save(complaint);
    }

    // ✅ Get complaints by user
    public List<Complaint> getComplaintsByUser(Long userId) {
        return complaintRepository.findByUserId(userId);
    }

    // ✅ Get all complaints (Admin)
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    // ✅ Update complaint status (Admin)
    public Complaint updateComplaintStatus(Long complaintId, String status) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found with ID: " + complaintId));
        complaint.setStatus(status);
        return complaintRepository.save(complaint);
    }
}
