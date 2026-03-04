package com.gabr.ecommerce.service.camunda;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CamundaOrderProcessService {
    private final RestClient camunda;


    public void startOrderProcess(Long orderId,String username , String email){
        Map<String, Object> body = Map.of(
                "variables", Map.of(
                        "orderId", Map.of("value", orderId, "type", "Long"),
                        "username", Map.of("value", username, "type", "String"),
                        "customerEmail", Map.of("value", email, "type", "String")
                ),
                "businessKey", "ORDER-" + orderId
        );
        camunda.post()
                .uri("/process-definition/key/{key}/start", "order_fulfillment")//Process_0zgilsu
                .body(body)
                .retrieve()
                .toBodilessEntity();

    }

    public void paymentReceived(Long orderId){
        Map<String, Object> body = Map.of(
                "messageName", "PaymentReceived",
                "businessKey", "ORDER-" + orderId
        );

        camunda.post()
                .uri("/message")
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
    public String findApproveTaskId(Long orderId) {
        // /task?processVariables=orderId_eq_123
        String businessKey = "ORDER-" + orderId;
        List<Map<String, Object>> tasks = camunda.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/task")
                        .queryParam("processInstanceBusinessKey", businessKey)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (tasks == null || tasks.isEmpty()) return null;
        return (String) tasks.get(0).get("id");
    }

    public void approveShipment(Long orderId, boolean approved) {
        String taskId = findApproveTaskId(orderId);
        if (taskId == null) throw new RuntimeException("No active approval task for order " + orderId);

        Map<String, Object> body = Map.of(
                "variables", Map.of(
                        "approved", Map.of("value", approved, "type", "Boolean")
                )
        );

        camunda.post()
                .uri("/task/{id}/complete", taskId)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

}
