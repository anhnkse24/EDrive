package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${frontend.url.dealer.verification:http://localhost:8080/api/auth/verify-dealer}")
    private String dealerVerificationUrl;



    @Override
    public void sendDealerApprovalRequestToAdmin(String adminEmail, String dealerName, String fullName, String email, String phone, String address, String verificationToken) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(adminEmail);
        msg.setSubject("🔔 Yêu cầu phê duyệt đại lý mới: " + dealerName);
        
        String verificationUrl = dealerVerificationUrl + "?token=" + verificationToken;
        
        msg.setText("Có một đại lý mới đăng ký tài khoản trên hệ thống!\n\n" +
                "THÔNG TIN ĐẠI LÝ:\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "Tên đại lý: " + dealerName + "\n" +
                "Người liên hệ: " + fullName + "\n" +
                "Email: " + email + "\n" +
                "Số điện thoại: " + phone + "\n" +
                "Địa chỉ: " + address + "\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "Vui lòng nhấp vào liên kết sau để PHÊ DUYỆT tài khoản:\n" +
                verificationUrl + "\n\n" +
                "Lưu ý: Liên kết này có hiệu lực trong 24 giờ.\n\n" +
                "Trân trọng,\n" +
                "Hệ thống EDrive");
        
        mailSender.send(msg);
    }
}