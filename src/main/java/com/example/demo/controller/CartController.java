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
    
    // Nota: El ID de usuario (userId) debería obtenerse de la sesión 
    // o un token de seguridad, no pasarse en la URL/Body.
    private final String MOCK_USER_ID = "12345";

    @Autowired
    private CartService cartService;
    
    // GET /api/cart
    @GetMapping
    public Cart getCart() {
        return cartService.getOrCreateCart(MOCK_USER_ID);
    }

    // POST /api/cart/add - Agregar producto al carrito [cite: 69]
    @PostMapping("/add")
    public Cart addItemToCart(@RequestParam String productId, @RequestParam int quantity) {
        return cartService.addItem(MOCK_USER_ID, productId, quantity);
    }

    // POST /api/cart/update - Modificar cantidad [cite: 71]
    @PostMapping("/update")
    public Cart updateItemQuantity(@RequestParam String productId, @RequestParam int quantity) {
        // En una implementación real, se llamaría a un método del CartService 
        // para modificar la cantidad
        return getCart(); 
    }

    // POST /api/cart/remove - Eliminar del carrito [cite: 73]
    @PostMapping("/remove")
    public Cart removeItem(@RequestParam String productId) {
        // En una implementación real, se llamaría a un método del CartService 
        // para eliminar un ItemCarrito
        return getCart(); 
    }

    // POST /api/cart/checkout - Realizar compra (checkout) [cite: 76]
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