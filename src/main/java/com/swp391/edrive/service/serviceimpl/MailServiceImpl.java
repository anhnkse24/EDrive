package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendDealerRegistrationMail(String to, String name, String email, String phone, String address) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("🆕 Đại lý mới đăng ký: " + name);
        msg.setText("Thông tin đại lý:\n" +
                "Tên: " + name + "\n" +
                "Email: " + email + "\n" +
                "SĐT: " + phone + "\n" +
                "Địa chỉ: " + address);
        mailSender.send(msg);
    }
}