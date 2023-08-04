package com.mypill.domain.email.service;

public interface EmailSenderService {
    void send(String to, String from, String title, String body);
}