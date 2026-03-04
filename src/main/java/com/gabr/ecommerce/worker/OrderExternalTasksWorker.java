//package com.gabr.ecommerce.worker;
//
//import com.gabr.ecommerce.constant.OrderStatus;
//import com.gabr.ecommerce.dto.camunda.ExternalTask;
//import com.gabr.ecommerce.repository.OrderRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestClient;
//
//import java.util.List;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//@EnableScheduling
//public class OrderExternalTasksWorker {
//
//    private final RestClient camunda;
//    private final OrderRepository orderRepository;
//
//    private final String workerId = "ecommerce-worker";
//
//    private static final Map<String, OrderStatus> TOPIC_TO_STATUS = Map.of(
//            "order.setStatusNew", OrderStatus.NEW,
//            "order.setStatusPaid", OrderStatus.PAID,
//            "order.setStatusShipped", OrderStatus.SHIPPED,
//            "order.setStatusCompleted", OrderStatus.COMPLETED,
//            "order.autoCancel", OrderStatus.CANCELED
//    );
//
//    @Scheduled(fixedDelay = 1500)
//    public void poll() {
//        Map<String, Object> req = Map.of(
//                "workerId", workerId,
//                "maxTasks", 5,
//                "usePriority", true,
//                "asyncResponseTimeout", 10000,
//                "topics", List.of(
//                        topic("order.setStatusNew"),
//                        topic("order.setStatusPaid"),
//                        topic("order.setStatusShipped"),
//                        topic("order.setStatusCompleted"),
//                        topic("order.autoCancel"),
//                        topic("order.reminder-notification")
//                )
//        );
//
//        List<ExternalTask> tasks = camunda.post()
//                .uri("/external-task/fetchAndLock")
//                .body(req)
//                .retrieve()
//                .body(new ParameterizedTypeReference<List<ExternalTask>>() {});
//
//        if (tasks == null || tasks.isEmpty()) return;
//
//        for (ExternalTask t : tasks) {
//            try {
//                handleTask(t);
//                complete(t.getId());
//            } catch (Exception ex) {
//                fail(t.getId(), ex.getMessage());
//            }
//        }
//    }
//
//    private Map<String, Object> topic(String name) {
//        return Map.of(
//                "topicName", name,
//                "lockDuration", 20000,
//                "variables", List.of("orderId")
//        );
//    }
//
//    private void handleTask(ExternalTask t) {
//        Long orderId = ((Number) t.getVariables().get("orderId").getValue()).longValue();
//
//        if ("order.reminder-notification".equals(t.getTopicName())) {
//            // TODO: send notification (email/sms/push) later
//            // For now: log only
//            System.out.println("Reminder for orderId=" + orderId);
//            return;
//        }
//
//        OrderStatus newStatus = TOPIC_TO_STATUS.get(t.getTopicName());
//        if (newStatus == null) throw new RuntimeException("Unknown topic " + t.getTopicName());
//
//        var order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
//        order.setStatus(newStatus);
//        orderRepository.save(order);
//    }
//
//    private void complete(String externalTaskId) {
//        camunda.post()
//                .uri("/external-task/{id}/complete", externalTaskId)
//                .body(Map.of("workerId", workerId))
//                .retrieve()
//                .toBodilessEntity();
//    }
//
//    private void fail(String externalTaskId, String msg) {
//        camunda.post()
//                .uri("/external-task/{id}/failure", externalTaskId)
//                .body(Map.of(
//                        "workerId", workerId,
//                        "errorMessage", msg,
//                        "retries", 3,
//                        "retryTimeout", 5000
//                ))
//                .retrieve()
//                .toBodilessEntity();
//    }
//}
