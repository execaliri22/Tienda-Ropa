package com.example.demo.controller;

import com.example.demo.model.Cart;
import com.example.demo.model.Order;
import com.example.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    private final String MOCK_USER_ID = "12345";

    @Autowired
    private CartService cartService;
    
    // GET /api/cart
    @GetMapping
    public Cart getCart() {
        return cartService.getOrCreateCart(MOCK_USER_ID);
    }

    // POST /api/cart/add - Agregar producto al carrito
    @PostMapping("/add")
    public Cart addItemToCart(@RequestParam String productId, @RequestParam int quantity) {
        return cartService.addItem(MOCK_USER_ID, productId, quantity);
    }

    // POST /api/cart/update - Modificar cantidad (Implementado)
    @PostMapping("/update")
    public Cart updateItemQuantity(@RequestParam String productId, @RequestParam int quantity) {
        return cartService.updateItemQuantity(MOCK_USER_ID, productId, quantity);
    }

    // POST /api/cart/remove - Eliminar del carrito (Implementado)
    @PostMapping("/remove")
    public Cart removeItem(@RequestParam String productId) {
        return cartService.removeItem(MOCK_USER_ID, productId);
    }

    // POST /api/cart/checkout - Realizar compra (checkout)
    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout() {
        try {
            Order order = cartService.checkout(MOCK_USER_ID);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}