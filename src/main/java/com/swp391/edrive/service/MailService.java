package com.swp391.edrive.service;

public interface MailService {
    void sendDealerRegistrationMail(String to, String dealerName, String email, String phone, String address);
}