package com.yourstore.scheduler;

import com.yourstore.entity.Order;
import com.yourstore.enums.OrderStatus;
import com.yourstore.repository.OrderRepository;
import com.yourstore.service.EmailService;
import com.yourstore.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ShippingScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ShippingScheduler.class);

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final EmailService emailService;

    public ShippingScheduler(OrderRepository orderRepository,
                             OrderService orderService,
                             EmailService emailService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 6 */2 * *", zone = "Asia/Riyadh")
    public void runShippingBatch() {
        logger.info("🚀 Starting shipping batch job at: {}", LocalDateTime.now());

        try {
            // Step 1: Fetch all READY orders that haven't been processed
            List<Order> readyOrders = orderRepository.findByStatusAndBatchProcessedFalse(OrderStatus.READY);

            if (readyOrders.isEmpty()) {
                logger.info("✅ No orders ready for shipping. Batch job completed.");
                return;
            }

            logger.info("📦 Found {} orders ready for shipping", readyOrders.size());

            // Step 2: Group orders by city
            Map<String, List<Order>> ordersByCity = readyOrders.stream()
                    .collect(Collectors.groupingBy(order ->
                            order.getCity() != null ? order.getCity().getName() : "Unknown"
                    ));

            logger.info("🏙️ Orders grouped by {} cities", ordersByCity.size());

            // Step 3: Generate shipping labels per city
            for (Map.Entry<String, List<Order>> entry : ordersByCity.entrySet()) {
                String city = entry.getKey();
                List<Order> cityOrders = entry.getValue();
                logger.info("📋 Shipping list for {}: {} orders", city, cityOrders.size());
                // In real implementation, print shipping labels here
            }

            // Step 4: Mark orders as shipped
            orderService.markOrdersAsShipped(readyOrders);

            logger.info("✅ Shipping batch job completed. {} orders processed.", readyOrders.size());

        } catch (Exception e) {
            logger.error("❌ Shipping batch job failed: {}", e.getMessage(), e);
            emailService.sendAutomationFailureAlert("Shipping batch job failed: " + e.getMessage());
        }
    }
}