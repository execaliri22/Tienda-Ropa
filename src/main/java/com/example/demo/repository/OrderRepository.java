package com.example.demo.repository;

import com.example.demo.model.Order;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    // Para verHistorialPedidos() [cite: 121]
    List<Order> findByUserId(String userId); 
}