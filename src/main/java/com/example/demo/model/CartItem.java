package com.example.demo.model;

// No es @Document, será una clase embebida en Cart
public class CartItem {

    private String productId; // Representa idProducto (FK)
    private int cantidad;
    private double subtotal;

    // Getters y Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    
    // Método para actualizar cantidad [cite: 140]
    public void actualizarCantidad(int nuevaCant) {
        this.cantidad = nuevaCant;
        // La lógica de actualizar subtotal se manejaría en el CartService
    }
}