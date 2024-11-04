package com.inventory.service;

import com.inventory.entity.Inventory;
import com.inventory.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private KafkaTemplate<String, Long> kafkaTemplate;

    private final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    @KafkaListener(topics = "order-placed", groupId = "inventory")
    public void consumeOrderPlaced(Map<String, Object> message) {
        Long orderId = ((Number) message.get("orderId")).longValue();
        String product = (String) message.get("product");
        int qty = (Integer) message.get("qty");

        Optional<Inventory> productOpt = inventoryRepository.findByProduct(product);
        if (productOpt.isPresent()) {
            Inventory inventory = productOpt.get();
            logger.info("{} id {} stock {}", inventory.getProduct(), inventory.getId(), inventory.getStock());

            if (inventory.getStock() == 0) {
                throw new RuntimeException("Out of stock");
            }
            if (qty > inventory.getStock()) {
                throw new RuntimeException("Not enough stock. Max available: " + inventory.getStock());
            } else {
                inventory.setStock(inventory.getStock() - qty);
                Inventory savedInventory = inventoryRepository.save(inventory);

                kafkaTemplate.send("order", orderId);
                logger.info("Updated stock for product: {}. Remaining stock: {}", savedInventory.getProduct(), savedInventory.getStock());
            }
        } else {
            throw new RuntimeException("Product not found");
        }
    }

}
