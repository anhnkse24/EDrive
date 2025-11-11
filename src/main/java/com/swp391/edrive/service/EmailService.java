package com.swp391.edrive.service;

import java.io.File;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
    void sendEmailWithAttachment(String to, String subject, String text, File attachment);
}