package com.example.controller;

import com.example.entity.Order;
import com.example.entity.Product;
import com.example.service.OrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private static final Logger logger = LogManager.getLogger(OrderController.class);

    private final JdbcTemplate h2JdbcTemplate;

    public OrderController(OrderService orderService,
                           @Qualifier("h2DataSource") DataSource h2DataSource) {
        this.orderService = orderService;
        this.h2JdbcTemplate = new JdbcTemplate(h2DataSource);
    }

    // Oracle CRUD

    @PostMapping
    public Order createOrder(@Valid @RequestBody Order order) {
        logger.info("Creating order: {}", order.getCustomerName());
        return orderService.saveOrder(order);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        logger.info("Getting all orders from Oracle DB");
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        logger.info("Getting order with ID {} from Oracle DB", id);
        Order order = orderService.getOrderById(id);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order updatedOrder) {
        logger.info("Updating order with ID {} in Oracle DB", id);
        Order existingOrder = orderService.getOrderById(id);

        if (existingOrder == null) {
            return ResponseEntity.notFound().build();
        }

        existingOrder.setCustomerName(updatedOrder.getCustomerName());
        existingOrder.setOrderName(updatedOrder.getOrderName());
        existingOrder.setPrice(updatedOrder.getPrice());

        if (updatedOrder.getProducts() != null) {
            for (Product p : updatedOrder.getProducts()) {
                p.setOrder(existingOrder);
            }
            existingOrder.setProducts(updatedOrder.getProducts());
        }

        Order savedOrder = orderService.saveOrder(existingOrder);
        return ResponseEntity.ok(savedOrder);
    }

    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Long id) {
        logger.info("Deleting order with ID {} from Oracle DB", id);
        orderService.deleteOrder(id);
        return "Order with ID " + id + " deleted successfully!";
    }

    // ---------------- H2 CRUD ----------------

    @GetMapping("/h2")
    public List<Map<String, Object>> getAllOrdersH2() {
        logger.info("Fetching all orders from H2 DB");
        return h2JdbcTemplate.queryForList("SELECT * FROM ORDER_TABLE");
    }

    @PostMapping("/h2")
    public String createOrderH2(@Valid @RequestBody Order order) {
        logger.info("Inserting order into H2 DB: {}", order.getCustomerName());
        String sql = "INSERT INTO ORDER_TABLE (CUSTOMER_NAME) VALUES (?)";
        h2JdbcTemplate.update(sql, order.getCustomerName());
        return "Order inserted into H2 successfully!";
    }

    @PutMapping("/h2/{id}")
    public String updateOrderH2(@PathVariable Long id, @Valid @RequestBody Order order) {
        logger.info("Updating order ID {} in H2 DB", id);
        String sql = "UPDATE ORDER_TABLE SET CUSTOMER_NAME = ? WHERE ID = ?";
        int updated = h2JdbcTemplate.update(sql, order.getCustomerName(), id);
        return updated > 0 ? "Order updated in H2 successfully!" : "Order not found in H2";
    }

    @DeleteMapping("/h2/{id}")
    public String deleteOrderH2(@PathVariable Long id) {
        logger.info("Deleting order ID {} from H2 DB", id);
        String sql = "DELETE FROM ORDER_TABLE WHERE ID = ?";
        int deleted = h2JdbcTemplate.update(sql, id);
        return deleted > 0 ? "Order deleted from H2 successfully!" : "Order not found in H2";
    }
}
