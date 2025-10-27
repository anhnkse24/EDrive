package com.swp391.edrive.service;

public interface MailService {

    void sendDealerApprovalRequestToAdmin(String adminEmail, String dealerName, String fullName, String email, String phone, String address, String verificationToken);
}