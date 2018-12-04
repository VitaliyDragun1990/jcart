package com.revenat.jcart.core.common.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class SimpleEmailService implements EmailService {

    private static final JCLogger LOGGER = JCLogger.getLogger(SimpleEmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    private String supportEmail;

    @Value("${support.email}")
    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

    public void sendEmail(String to, String subject, String content) {
        try {
            // Prepare message using a Spring helper
            final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
            message.setSubject(subject);
            message.setFrom(supportEmail);
            message.setTo(to);
            message.setText(content, true);

            this.javaMailSender.send(message.getMimeMessage());
        } catch (MailException | MessagingException e) {
            LOGGER.error("Error during sending email.", e);
        }
    }

}
