package com.purna.hostel.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ================================
    // 1. OTP Email
    // ================================
    public void sendOtpEmail(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("HostelEase - OTP Verification");
        message.setText("Your OTP is: " + otp + "\nValid for 5 minutes.");

        mailSender.send(message);
    }

    // ================================
    // 2. General Email
    // ================================
    public void sendEmail(String toEmail, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    // ================================
    // 3. Leave Applied Email
    // ================================
    public void sendLeaveAppliedEmail(String toEmail, String studentName) {

        String subject = "Leave Application Submitted Successfully";
        String body = "Dear " + studentName + ",\n\n"
                + "Your leave request has been submitted successfully.\n"
                + "Please wait for warden approval.\n\n"
                + "Regards,\nHostel Management";

        sendEmail(toEmail, subject, body);
    }

    // ================================
    // 4. Leave Status Update Email
    // ================================
    public void sendLeaveStatusEmail(String toEmail, String studentName, String status) {

        String subject = "Leave Request Status Updated";
        String body = "Dear " + studentName + ",\n\n"
                + "Your leave request status has been updated to: " + status + "\n\n"
                + "Regards,\nHostel Management";

        sendEmail(toEmail, subject, body);
    }

    // ================================
    // 5. Gate Pass Email (PDF)
    // ================================
    public void sendGatePassEmail(String toEmail,
                                  String studentName,
                                  byte[] pdfBytes) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("Leave Approved - Gate Pass Generated");

        String body = "Dear " + studentName + ",\n\n"
                + "Your leave request has been APPROVED.\n"
                + "Gate Pass is attached with this email.\n\n"
                + "Regards,\nHostel Management";

        helper.setText(body);

        helper.addAttachment("GatePass.pdf",
                new ByteArrayResource(pdfBytes));

        mailSender.send(message);
    }

    // ================================
    // 6. 🚨 ABSENT ATTENDANCE EMAIL
    // ================================
    public void sendAbsentAttendanceEmail(String toEmail,
                                          String studentName,
                                          String date) {

        String subject = "⚠ Attendance Alert - Marked Absent";

        String body = "Dear " + studentName + ",\n\n"
                + "You were marked ABSENT on " + date + ".\n\n"
                + "If this is incorrect, please contact the hostel office immediately.\n\n"
                + "Regards,\nHostel Management";

        sendEmail(toEmail, subject, body);
    }
}