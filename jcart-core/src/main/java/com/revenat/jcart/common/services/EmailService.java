package com.revenat.jcart.common.services;

public interface EmailService {

    void sendEmail(String to, String subject, String content);
}
