package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Document(collection = "carts")
public class Cart {

    @Id
    private String id; // Representa idCarrito

    private String userId; // Referencia al Usuario (posee/tiene)
    private Date fechaCreacion = new Date(); // fechaCreacion: Date
    private List<CartItem> items; // Contiene ItemCarrito

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
    
    // Las operaciones de Carrito (agregarItem, checkout, vaciarCarrito) 
    // se implementan en el CartService.
}