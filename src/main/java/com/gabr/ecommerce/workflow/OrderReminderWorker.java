package com.gabr.ecommerce.workflow;

import com.gabr.ecommerce.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription("order.reminder-notification")
@RequiredArgsConstructor
public class OrderReminderWorker implements ExternalTaskHandler {

    private final EmailService emailService;

    @Override
    public void execute(ExternalTask task, ExternalTaskService service) {
        Long orderId = task.getVariable("orderId");

        String username = task.getVariable("username");
        String email = task.getVariable("customerEmail");
        String approvalUrl = "http://localhost:8080/approve?orderId=" + orderId;
        Double orderTotal = task.getVariable("orderTotal");

        if (email == null || email.isBlank()) {
            System.err.println("No email found for order " + orderId + " - skipping email");
            service.complete(task);
            return;
        }

        // ابعت الإيميل asynchronously (الـ method @Async)
        emailService.sendReminderEmail(email, username != null ? username : "Customer",
                orderId, approvalUrl, orderTotal != null ? orderTotal : 0.0);

        // complete الـ task (حتى لو الإيميل فشل، عادةً نكمل الـ process)
        service.complete(task, java.util.Map.of("reminderSent", true));
    }
}
