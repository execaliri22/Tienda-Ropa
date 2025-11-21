package com.example.demo.repository;

import com.example.demo.model.Wishlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistRepository extends MongoRepository<Wishlist, String> {
    // Método para encontrar la Wishlist asociada a un ID de usuario específico
    Wishlist findByUserId(String userId);
}