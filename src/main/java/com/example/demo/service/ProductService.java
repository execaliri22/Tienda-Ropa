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
    
    /**
     * Busca y filtra productos por ID de categoría y/o término de búsqueda por nombre.
     */
    public List<Product> findProducts(String categoryId, String searchTerm) {
        
        // 1. Filtrar solo por Categoría
        if (categoryId != null && !categoryId.isEmpty()) {
            return productRepository.findByCategoriaId(categoryId);
        }
        
        // 2. Buscar solo por Nombre (usando regex para búsqueda flexible)
        if (searchTerm != null && !searchTerm.isEmpty()) {
            // Utilizamos el modificador 'i' para búsqueda case-insensitive
            String regex = String.format(".*%s.*", searchTerm);
            return productRepository.findByNombreRegex(regex);
        }
        
        // 3. Por defecto: Devolver todos los productos
        return productRepository.findAll();
    }
    
    // Método esencial para el checkout (actualizarStock)
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