package com.example.proyectspring.service;

import java.io.File;

public interface IEmailService {
    void sendEmail(String toUser, String subject, String message);
    
    void sendHtmlEmail(String toUser, String subject, String htmlContent);

    void sendEmailWithFile(String toUser, String subject, String message, File file);
}
