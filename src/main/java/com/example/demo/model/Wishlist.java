package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "wishlists")
public class Wishlist {

    @Id
    private String id;

    // Referencia al ID del usuario
    private String userId;

    // Lista de productos favoritos. Usamos @DBRef para hacer referencia a los objetos Producto
    @DBRef
    private List<Product> productos;

    public Wishlist() {
        this.productos = new ArrayList<>();
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<Product> getProductos() { return productos; }
    public void setProductos(List<Product> productos) { this.productos = productos; }
}