package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Document(collection = "orders")
public class Order {

    @Id
    private String id; // Representa idPedido

    private String userId; // Referencia al Usuario (posee)
    private Date fecha = new Date(); 
    private double total;
    private String estado;
    private List<OrderItem> items; // Contiene ItemPedido

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    // Método para calcular total [cite: 132]
    public double calcularTotal() {
        return total; // En un modelo real, este método sumaría los subtotales
    }
    
    // La funcionalidad generarFactura(): PDF [cite: 133] se implementaría en el OrderService.
}