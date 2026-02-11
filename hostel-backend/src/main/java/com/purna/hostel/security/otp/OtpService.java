package com.purna.hostel.security.otp;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    private static final int OTP_EXPIRY_MINUTES = 5;

    private final Map<String, OtpData> otpStore = new HashMap<>();

    public String generateOtp(String email) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        otpStore.put(email, new OtpData(otp, LocalDateTime.now()));
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        OtpData data = otpStore.get(email);

        if (data == null) return false;

        if (data.getCreatedAt().plusMinutes(OTP_EXPIRY_MINUTES).isBefore(LocalDateTime.now())) {
            otpStore.remove(email);
            return false;
        }

        boolean isValid = data.getOtp().equals(otp);
        if (isValid) otpStore.remove(email);

        return isValid;
    }

    private static class OtpData {
        private final String otp;
        private final LocalDateTime createdAt;

        public OtpData(String otp, LocalDateTime createdAt) {
            this.otp = otp;
            this.createdAt = createdAt;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }
}
