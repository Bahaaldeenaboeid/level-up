package com.yourstore.pattern.template;

import com.yourstore.entity.Order;
import com.yourstore.service.EmailService;
import com.yourstore.service.OrderService;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConcreteShippingBatchJob extends ShippingBatchJobBase {

    private static final Logger logger = LoggerFactory.getLogger(ConcreteShippingBatchJob.class);

    private final OrderService orderService;
    private final EmailService emailService;

    public ConcreteShippingBatchJob(OrderService orderService, EmailService emailService) {
        this.orderService = orderService;
        this.emailService = emailService;
    }

    @Override
    protected List<Order> getReadyOrders() {
        logger.info("Fetching ready orders for shipping batch job");
        return orderService.getReadyOrdersForBatch();
    }

    @Override
    protected void generateLabels(Map<String, List<Order>> ordersByCity) {
        logger.info("Generating shipping labels for {} cities", ordersByCity.size());
        for (Map.Entry<String, List<Order>> entry : ordersByCity.entrySet()) {
            String city = entry.getKey();
            for (Order order : entry.getValue()) {
                logger.info("Generating label for order {} in city {}", order.getId(), city);
                // Implement label generation logic as needed
            }
        }
    }

    @Override
    protected void notifyCustomers(List<Order> orders) {
        logger.info("Sending shipping notifications for {} orders", orders.size());
        for (Order order : orders) {
            emailService.sendOrderShippedEmail(order.getId());
        }
    }
}
