package com.purna.hostel.service;

import com.purna.hostel.entity.Complaint;
import com.purna.hostel.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    // ✅ Save complaint
    public Complaint saveComplaint(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    // ✅ Get complaints by student ID
    public List<Complaint> getComplaintsByUser(Long userId) {
        return complaintRepository.findByUser_Id(userId);
    }

    // ✅ Get all complaints (Warden/Admin)
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    // ✅ Get complaint by ID
    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id).orElse(null);
    }

    // ✅ Update complaint status
    public Complaint updateComplaintStatus(Long complaintId, String status) {

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElse(null);

        if (complaint == null) {
            return null;
        }

        complaint.setStatus(status);
        return complaintRepository.save(complaint);
    }

    // ✅ Delete complaint
    public void deleteComplaint(Long id) {
        complaintRepository.deleteById(id);
    }
}