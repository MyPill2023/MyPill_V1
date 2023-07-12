package com.mypill.domain.email.emailsender.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class GmailEmailSenderService implements EmailSenderService {
    private final JavaMailSender mailSender;

    @Override
    public void send(String to, String from, String title, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(from);
        message.setSubject(title);
        message.setText(body);
        mailSender.send(message);
    }
}