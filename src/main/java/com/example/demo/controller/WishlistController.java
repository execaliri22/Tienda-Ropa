package com.example.demo.controller;

import com.example.demo.model.Wishlist;
import com.example.demo.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/wishlist") // Ruta con ID dinámico
public class WishlistController {
    
    // private final String MOCK_USER_ID = "12345"; // Eliminado

    @Autowired
    private WishlistService wishlistService;

    // GET /api/users/{userId}/wishlist - Ver la lista completa
    @GetMapping
    public ResponseEntity<Wishlist> getWishlist(@PathVariable String userId) {
        Wishlist wishlist = wishlistService.getOrCreateWishlist(userId);
        return ResponseEntity.ok(wishlist);
    }

    // POST /api/users/{userId}/wishlist/add?productId=... - Agregar a favoritos
    @PostMapping("/add")
    public ResponseEntity<Wishlist> addProduct(@PathVariable String userId, @RequestParam String productId) {
        Wishlist updatedWishlist = wishlistService.addProductToWishlist(userId, productId);
        return ResponseEntity.ok(updatedWishlist);
    }

    // DELETE /api/users/{userId}/wishlist/remove?productId=... - Eliminar de favoritos
    @DeleteMapping("/remove")
    public ResponseEntity<Wishlist> removeProduct(@PathVariable String userId, @RequestParam String productId) {
        Wishlist updatedWishlist = wishlistService.removeProductFromWishlist(userId, productId);
        return ResponseEntity.ok(updatedWishlist);
    }
}