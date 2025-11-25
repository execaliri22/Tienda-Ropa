package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.util.List;

@Document(collection = "users")
public class User {

    @Id
    private String id; // idUsuario

    private String nombre;
    private String email; 
    private String password; // Representa contrasenaHash
    private String direccion; 
    
    // NUEVO: Campo para la URL de la foto de perfil (relativa a /static/)
    private String profilePictureUrl; 

    // Relación 'posee' con Pedido (Order), cargada automáticamente por MongoDB
    @DBRef
    private List<Order> pedidos; 

    public User() {}

    public User(String nombre, String email, String password, String direccion) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.direccion = direccion;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    // NUEVOS Getters y Setters para la foto
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    
    // Getters y Setters para la nueva lista de pedidos
    public List<Order> getPedidos() { return pedidos; }
    public void setPedidos(List<Order> pedidos) { this.pedidos = pedidos; }
}