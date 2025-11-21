package com.example.demo.service;

import com.example.demo.model.Cart;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    // Lógica para crear el pedido (parte de checkout())
    public Order createOrderFromCart(Cart cart) {
        Order order = new Order();
        order.setUserId(cart.getUserId());
        order.setEstado("PENDIENTE");
        
        // Convertir CartItems a OrderItems y calcular total
        double total = 0;
        List<OrderItem> orderItems = new ArrayList<>();
        cart.getItems().forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setCantidad(cartItem.getCantidad());
            orderItem.setSubtotal(cartItem.getSubtotal());
            orderItems.add(orderItem);
        });
        
        // Sumar subtotales después de crear la lista de OrderItem para evitar
        // capturar y mutar una variable local dentro del lambda
        for (OrderItem oi : orderItems) {
            total += oi.getSubtotal();
        }
        
        order.setItems(orderItems);
        order.setTotal(total); // total: Decimal [cite: 130]
        
        return orderRepository.save(order);
    }

    // Lógica para verHistorialPedidos(): List<Pedido> [cite: 121]
    public List<Order> getHistory(String userId) {
        return orderRepository.findByUserId(userId);
    }
    
    // Lógica para generarFactura(): PDF [cite: 133] (Requiere una librería de PDF)
    public byte[] generateInvoice(String orderId) {
        // Implementación de generación de PDF aquí...
        return "Factura en PDF simulada".getBytes();
    }
}