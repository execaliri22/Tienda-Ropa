package com.example.demo.model;

// No es @Document, será una clase embebida en Order
public class OrderItem {

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

    // Método para calcular subtotal [cite: 144]
    public double calcularSubtotal() {
        return subtotal; // En un modelo real, este método recalcularía el subtotal
    }
}