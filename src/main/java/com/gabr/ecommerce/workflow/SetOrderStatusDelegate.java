//package com.gabr.ecommerce.workflow;
//
//import com.gabr.ecommerce.repository.OrderRepository;
//import lombok.RequiredArgsConstructor;
//import org.camunda.bpm.engine.delegate.*;
//import org.springframework.stereotype.Component;
//
//@Component("setOrderStatusDelegate")
//@RequiredArgsConstructor
//public class SetOrderStatusDelegate implements JavaDelegate {
//
//    private final OrderRepository orderRepository;
//
//    @Override
//    public void execute(DelegateExecution execution) {
//        Long orderId = (Long) execution.getVariable("orderId");
//        String status = (String) execution.getVariable("status"); // field injection alternative below
//
//        // If using field injection via camunda:field, use:
//        // String status = (String) ((DelegateExecution) execution).getVariable("status");
//
//        // better: read from field injected parameter:
//        // (we will implement it properly below)
//    }
//}
