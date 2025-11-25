package com.example.demo.controller;

import com.example.demo.model.Cart;
import com.example.demo.model.Order;
import com.example.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/cart") // Ruta con ID dinámico
public class CartController {
    
    // private final String MOCK_USER_ID = "12345"; // Eliminado

    @Autowired
    private CartService cartService;
    
    // GET /api/users/{userId}/cart
    @GetMapping
    public Cart getCart(@PathVariable String userId) {
        return cartService.getOrCreateCart(userId);
    }

    // POST /api/users/{userId}/cart/add - Agregar producto al carrito
    @PostMapping("/add")
    public Cart addItemToCart(@PathVariable String userId, @RequestParam String productId, @RequestParam int quantity) {
        return cartService.addItem(userId, productId, quantity);
    }

    // POST /api/users/{userId}/cart/update - Modificar cantidad
    @PostMapping("/update")
    public Cart updateItemQuantity(@PathVariable String userId, @RequestParam String productId, @RequestParam int quantity) {
        return cartService.updateItemQuantity(userId, productId, quantity);
    }

    // POST /api/users/{userId}/cart/remove - Eliminar del carrito
    @PostMapping("/remove")
    public Cart removeItem(@PathVariable String userId, @RequestParam String productId) {
        return cartService.removeItem(userId, productId);
    }

    // POST /api/users/{userId}/cart/checkout - Realizar compra (checkout)
    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(@PathVariable String userId) {
        try {
            Order order = cartService.checkout(userId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}