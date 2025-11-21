package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.model.Wishlist;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;
    
    @Autowired
    private ProductRepository productRepository;

    // Obtiene la lista de deseos del usuario, o crea una nueva si no existe
    public Wishlist getOrCreateWishlist(String userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId);
        if (wishlist == null) {
            wishlist = new Wishlist();
            wishlist.setUserId(userId);
            wishlistRepository.save(wishlist);
        }
        return wishlist;
    }

    // Lógica para agregar productos a favoritos
    public Wishlist addProductToWishlist(String userId, String productId) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        Optional<Product> productOpt = productRepository.findById(productId);

        if (productOpt.isPresent()) {
            Product productToAdd = productOpt.get();
            boolean alreadyExists = wishlist.getProductos().stream()
                .anyMatch(p -> p.getId().equals(productId));
            
            if (!alreadyExists) {
                wishlist.getProductos().add(productToAdd);
                return wishlistRepository.save(wishlist);
            }
        }
        return wishlist; // Devuelve la lista sin cambios si no se encuentra o ya existe
    }

    // Lógica para eliminar productos de favoritos
    public Wishlist removeProductFromWishlist(String userId, String productId) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        
        wishlist.getProductos().removeIf(p -> p.getId().equals(productId));
        
        return wishlistRepository.save(wishlist);
    }
}