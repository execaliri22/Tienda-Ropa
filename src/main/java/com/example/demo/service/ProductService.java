package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }
    
    // Método esencial para el checkout (actualizarStock) [cite: 149]
    public void updateStock(String productId, int quantityChange) {
        Optional<Product> productOpt = productRepository.findById(productId);
        productOpt.ifPresent(product -> {
            product.actualizarStock(quantityChange);
            productRepository.save(product);
        });
    }
    
    // Métodos CRUD para la gestión de productos
    public Product createProduct(Product product) { return productRepository.save(product); }
    public void deleteProduct(String id) { productRepository.deleteById(id); }
}