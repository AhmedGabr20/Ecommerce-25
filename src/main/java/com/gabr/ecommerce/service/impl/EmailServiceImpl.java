package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;


//    @Override
//    public void sendWelcomeEmail(String to) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject("Welcome to E-Commerce App!");
//        message.setText("🎉 Your account has been successfully created!");
//        mailSender.send(message);
//    }

    @Override
    @Async
    public void sendWelcomeEmail(String to, String username) {
        try {
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("dashboardUrl", "http://localhost:8080/swagger-ui/index.html#/");

            // توليد المحتوى من القالب
            String htmlContent = templateEngine.process("welcome-email", context);

            // إعداد الرسالة
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("🎉 Welcome to E-Commerce App!");
            helper.setText(htmlContent, true);

            // إرسال الإيميل
            mailSender.send(message);

            System.out.println("✅ Welcome email sent successfully to " + to);
        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }

}
