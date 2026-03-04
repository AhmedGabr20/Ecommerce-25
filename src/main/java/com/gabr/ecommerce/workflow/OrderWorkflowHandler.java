package com.gabr.ecommerce.workflow;

import com.gabr.ecommerce.constant.OrderStatus;
import com.gabr.ecommerce.exception.BusinessException;
import com.gabr.ecommerce.exception.ErrorCode;
import com.gabr.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public abstract class OrderWorkflowHandler implements ExternalTaskHandler {

    protected final OrderRepository orderRepository;

    protected Long getOrderId(ExternalTask task) {
        Object v = task.getVariable("orderId");
        if (v instanceof Number n) return n.longValue();
        if (v instanceof String s) return Long.parseLong(s);
        throw new IllegalArgumentException("orderId variable missing/invalid");
    }

    protected void setStatus(Long orderId, OrderStatus status) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        order.setStatus(status);
        orderRepository.save(order);
    }

    // Inner class لكل topic (كل واحدة @Component و @ExternalTaskSubscription خاصة بها)

    @Component
    @ExternalTaskSubscription("order.setStatusNew")
    public static class SetNew extends OrderWorkflowHandler {
        public SetNew(OrderRepository orderRepository) {
            super(orderRepository);
        }

        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            Long orderId = getOrderId(task);
            setStatus(orderId, OrderStatus.NEW);
            service.complete(task);
        }
    }

    @Component
    @ExternalTaskSubscription("order.setStatusPaid")
    public static class SetPaid extends OrderWorkflowHandler {
        public SetPaid(OrderRepository orderRepository) {
            super(orderRepository);
        }

        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            Long orderId = getOrderId(task);
            setStatus(orderId, OrderStatus.PAID);
            service.complete(task);
        }
    }

    @Component
    @ExternalTaskSubscription("order.setStatusShipped")
    public static class SetShipped extends OrderWorkflowHandler {
        public SetShipped(OrderRepository orderRepository) {
            super(orderRepository);
        }

        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            Long orderId = getOrderId(task);
            setStatus(orderId, OrderStatus.SHIPPED);
            service.complete(task);
        }
    }

    @Component
    @ExternalTaskSubscription("order.setStatusCompleted")
    public static class SetCompleted extends OrderWorkflowHandler {
        public SetCompleted(OrderRepository orderRepository) {
            super(orderRepository);
        }

        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            Long orderId = getOrderId(task);
            setStatus(orderId, OrderStatus.COMPLETED);
            service.complete(task);
        }
    }

    @Component
    @ExternalTaskSubscription("order.autoCancel")
    public static class AutoCancel extends OrderWorkflowHandler {
        public AutoCancel(OrderRepository orderRepository) {
            super(orderRepository);
        }

        @Override
        public void execute(ExternalTask task, ExternalTaskService service) {
            Long orderId = getOrderId(task);
            setStatus(orderId, OrderStatus.CANCELED);
            service.complete(task);
        }
    }
}