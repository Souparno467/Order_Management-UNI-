package com.example.service;


import com.example.entity.Order;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.OrderRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private static final Logger logger = LogManager.getLogger(OrderService.class);

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order saveOrder(Order order) {
        logger.info("Saving order for: {}", order.getCustomerName());
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        logger.info("Fetching all orders");
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
    }

    public Order updateOrder(Long id, Order updatedOrder) {
        Order existingOrder = getOrderById(id);
        existingOrder.setCustomerName(updatedOrder.getCustomerName());
        existingOrder.setProducts(updatedOrder.getProducts());
        logger.info("Updating order with ID: {}", id);
        return orderRepository.save(existingOrder);
    }

    public void deleteOrder(Long id) {
        Order existingOrder = getOrderById(id);
        orderRepository.delete(existingOrder);
        logger.info("Deleted order with ID: {}", id);
    }
}