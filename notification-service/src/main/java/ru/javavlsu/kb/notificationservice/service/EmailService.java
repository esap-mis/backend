package ru.javavlsu.kb.notificationservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String email;

    private final JavaMailSender emailSender;
    private final Logger log = LoggerFactory.getLogger(EmailService.class);

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendEmail(String toAddress, String subject, String message) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(email);
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        try {
            emailSender.send(simpleMailMessage);
            log.info("Email notification sent successfully to: {token=" + toAddress + "}");
        } catch (Exception e) {
            log.error("Error sending email notification to {token=" + toAddress + "} : {error=" + e.getMessage() + "}");
        }
    }
}
