package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Document(collection = "products")
public class Product {

    @Id
    private String id; 

    private String nombre;
    private double precio;
    private int stock; 
    
    // AÑADIDO: Campo para almacenar la URL o ruta de la imagen
    private String imageUrl; 

    @DBRef 
    private Category categoria;

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    
    // Getter y Setter para imageUrl (NUEVO)
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public Category getCategoria() { return categoria; }
    public void setCategoria(Category categoria) { this.categoria = categoria; }
    
    // Método para actualizar stock
    public void actualizarStock(int cantidad) { 
        this.stock += cantidad; 
    }
}