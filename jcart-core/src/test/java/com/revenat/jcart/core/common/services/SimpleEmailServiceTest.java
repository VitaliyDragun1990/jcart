package com.revenat.jcart.core.common.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimpleEmailServiceTest {
    private static final String RECEIVER = "jack@gmail.com";
    private static final String SENDER = "anna@gmail.com";
    private static final String SUBJECT = "test";
    private static final String SUPPORT_EMAIL = "admin@gmail.com";

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private MimeMessage mimeMessage;
    @Captor
    private ArgumentCaptor<MimeMessage> messageCaptor;

    @InjectMocks
    private SimpleEmailService emailService;


    @Test
    public void sendEmail_ValidData_EmailSent() {
        emailService.setSupportEmail(SUPPORT_EMAIL);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendEmail(RECEIVER, SUBJECT, SENDER);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(messageCaptor.capture());

        MimeMessage sandedMessage = messageCaptor.getValue();
        assertThat(sandedMessage, equalTo(mimeMessage));
    }
}