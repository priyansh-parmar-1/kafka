package com.order.service;

import com.order.entity.Order;
import com.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    private final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public Order placeOrder(Order order) {
        order.setStatus("pending");
        orderRepository.save(order);
        logger.info("### -> adding the product with its quantity in the message");
        Map<String, Object> message = Map.of(
                "orderId", order.getId(),
                "qty", order.getQuantity(),
                "product", order.getProduct()
        );
        kafkaTemplate.send("order-placed", message);
        logger.info("### -> message passed");
        return order;
    }

    @KafkaListener(topics = "order", groupId = "order")
    public void reviseOrder(Long id) {

        Optional<Order> byId = orderRepository.findById(id);
        Order order = byId.get();
        order.setStatus("placed");
        orderRepository.save(order);

    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }
}
