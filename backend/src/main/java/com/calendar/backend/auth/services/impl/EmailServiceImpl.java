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
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final Configuration freemarkerConfig;

    @Async
    @Override
    public void sendClaimApprovedEmail(String recipient) {
        String htmlContent = generateEmailContent("email-template.ftl", Map.of(
                "header", "Application Approved",
                "content", "Congratulations! Your application has been approved."
        ));
        sendEmail(recipient, "Application approved!", htmlContent);
    }

    @Async
    @Override
    public void sendClaimRejectedEmail(String recipient) {
        String htmlContent = generateEmailContent("email-template.ftl", Map.of(
                "header", "Application Rejected",
                "content", "Unfortunately, your application has been rejected."
        ));
        sendEmail(recipient, "Application Rejected!", htmlContent);
    }

    @Async
    @Override
    public void sendClaimRequestEmail(String recipient) {
        String htmlContent = generateEmailContent("email-template.ftl", Map.of(
                "header", "Application Under Review",
                "content", "Your application has been received and is currently under review. You will be notified once a decision is made."
        ));
        sendEmail(recipient, "Application Under Review!", htmlContent);
    }

    @Async
    @Override
    public void sendSuccessfullyCreatedAccountEmail(String recipient) {
        String htmlContent = generateEmailContent("email-template.ftl", Map.of(
                "header", "Account Created Automatically",
                "content", "Your account has been created automatically."
        ));
        sendEmail(recipient, "Account Created Automatically!", htmlContent);
    }

    @Async
    @Override
    public void sendNewClaimEmail(String recipient) {
        String htmlContent = generateEmailContent("email-template.ftl", Map.of(
                "header", "New Application",
                "content", "A new application has been submitted and is awaiting review. Please check the system for details."
        ));
        sendEmail(recipient, "New Application!", htmlContent);
    }

    private void sendEmail(String recipient, String subject, String html) {
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
