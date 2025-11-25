package com.example.demo.controller;

import com.example.demo.model.Order;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/orders") // Ruta con ID dinámico
public class OrderController {

    // private final String MOCK_USER_ID = "12345"; // Eliminado
    
    @Autowired
    private OrderService orderService;

    // GET /api/users/{userId}/orders - Ver historial de pedidos 
    @GetMapping
    public List<Order> getOrderHistory(@PathVariable String userId) {
        return orderService.getHistory(userId);
    }
}