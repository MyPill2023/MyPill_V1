package com.mypill.domain.emailSender.service;

public interface EmailSenderService {
    void send(String to, String from, String title, String body);
}