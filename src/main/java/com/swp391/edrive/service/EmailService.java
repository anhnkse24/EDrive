package com.swp391.edrive.service;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
}