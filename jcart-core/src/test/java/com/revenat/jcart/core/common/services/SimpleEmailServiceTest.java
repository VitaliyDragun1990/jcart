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

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private MimeMessage mimeMessage;
    @Captor
    private ArgumentCaptor<MimeMessage> messageCaptor;

    @InjectMocks
    private SimpleEmailService emailService;

    @Test
    public void sendEmailPositive() {
        String to = "jack@gmail.com";
        String from = "anna@gmail.com";
        String subject = "test";
        emailService.setSupportEmail("admin@gmail.com");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendEmail(to, subject, from);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(messageCaptor.capture());

        MimeMessage sandedMessage = messageCaptor.getValue();
        assertThat(sandedMessage, equalTo(mimeMessage));
    }
}