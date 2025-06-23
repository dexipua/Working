package com.calendar.backend.auth.services.impl;

import com.calendar.backend.auth.services.inter.EmailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService { //TODO LOGGING + VERIFICATION

    private final JavaMailSender javaMailSender;
    private final Configuration freemarkerConfig;

    @Async
    @Override
    public void sendCodeEmail(String recipient, String code) {

        String htmlContent = generateEmailContent("email-template.ftl", Map.of(
                "header", "Application Approved",
                "content", "Your code is " + code
        ));
        sendEmail(recipient, htmlContent);
    }

    private void sendEmail(String recipient, String html) {
        String subject = "Code sent!";
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(html, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateEmailContent(String templateName, Map<String, Object> variables) {
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            StringWriter writer = new StringWriter();
            template.process(variables, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("Error generating email content", e);
        }
    }

}
