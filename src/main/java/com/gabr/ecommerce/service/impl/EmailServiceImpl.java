package com.gabr.ecommerce.service.impl;

import com.gabr.ecommerce.entity.OrderItem;
import com.gabr.ecommerce.reports.OrderInvoicePdf;
import com.gabr.ecommerce.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final OrderInvoicePdf orderInvoicePdf;


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

//    @Async
//    @Override
//    public void sendOrderConfirmation(String to, Long orderId, Double total) {
//        try {
//            // إعداد بيانات الـ context للـ template
//            Context context = new Context();
//            context.setVariable("orderId", orderId);
//            context.setVariable("total", total);
//            context.setVariable("orderUrl", "http://localhost:8080/api/orders/" + orderId);
//            context.setVariable("supportEmail", "support@ecommerce.com");
//
//            // توليد محتوى HTML من القالب
//            String htmlContent = templateEngine.process("order-confirmation", context);
//
//            // إعداد الرسالة
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//            helper.setTo(to);
//            helper.setSubject("🛒 Order Confirmation #" + orderId);
//            helper.setText(htmlContent, true);
//
//            // إرسال البريد الإلكتروني
//            mailSender.send(message);
//
//            log.info("✅ Order confirmation email sent successfully to {}", to);
//
//        } catch (MessagingException e) {
//            log.error("❌ Failed to send order confirmation email: {}", e.getMessage());
//        }
//    }

    @Override
    @Async
    public void sendOrderConfirmation(String to, Long orderId, Double total, List<OrderItem> items) {
        try {
            // 1️⃣ إعداد الـ Context للـ Template
            Context context = new Context();
            context.setVariable("orderId", orderId);
            context.setVariable("total", total);
            context.setVariable("items", items);
            context.setVariable("orderUrl", "http://localhost:8080/api/orders/" + orderId);
            context.setVariable("supportEmail", "support@ecommerce.com");

            String htmlContent = templateEngine.process("order-confirmation-invoice", context);

            // 2️⃣ إنشاء الفاتورة PDF
            ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream();
            orderInvoicePdf.createOrderInvoicePdf(orderId, total, items, pdfOutput);

            // 3️⃣ إعداد الرسالة بالإيميل
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("🧾 Your Order #" + orderId + " Invoice");
            helper.setText(htmlContent, true);

            // إضافة المرفق
            helper.addAttachment("Invoice-" + orderId + ".pdf", new ByteArrayResource(pdfOutput.toByteArray()));

            mailSender.send(message);
            log.info("✅ Order confirmation + PDF invoice sent to {}", to);

        } catch (Exception e) {
            log.error("❌ Failed to send order confirmation with invoice: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void sendReminderEmail(String to, String username, Long orderId, String approvalUrl, Double orderTotal) {
        try {
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("orderId", orderId);
            context.setVariable("approvalUrl", approvalUrl);
            context.setVariable("orderTotal", orderTotal);

            String htmlContent = templateEngine.process("reminder-email", context);  // اسم الـ template بدون .html

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("🔔 Reminder: Approve Your Order #" + orderId);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            System.out.println("✅ Reminder email sent successfully to " + to + " for order " + orderId);
        } catch (MessagingException e) {
            log.error("❌ Failed to send reminder email to " + to + ": " + e.getMessage());
        }
    }
}
