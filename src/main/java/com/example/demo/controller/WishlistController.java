package com.example.demo.controller;

import com.example.demo.model.Wishlist;
import com.example.demo.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {
    
    // Usamos el ID de usuario mock para simular la sesión
    private final String MOCK_USER_ID = "12345"; 

    @Autowired
    private WishlistService wishlistService;

    // GET /api/wishlist - Ver la lista completa
    @GetMapping
    public ResponseEntity<Wishlist> getWishlist() {
        Wishlist wishlist = wishlistService.getOrCreateWishlist(MOCK_USER_ID);
        return ResponseEntity.ok(wishlist);
    }

    // POST /api/wishlist/add?productId=... - Agregar a favoritos
    @PostMapping("/add")
    public ResponseEntity<Wishlist> addProduct(@RequestParam String productId) {
        Wishlist updatedWishlist = wishlistService.addProductToWishlist(MOCK_USER_ID, productId);
        return ResponseEntity.ok(updatedWishlist);
    }

    // DELETE /api/wishlist/remove?productId=... - Eliminar de favoritos
    @DeleteMapping("/remove")
    public ResponseEntity<Wishlist> removeProduct(@RequestParam String productId) {
        Wishlist updatedWishlist = wishlistService.removeProductFromWishlist(MOCK_USER_ID, productId);
        return ResponseEntity.ok(updatedWishlist);
    }
}