package com.example.demo.service;

import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Order;
import com.example.demo.model.Product;
import com.example.demo.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@SuppressWarnings("unused")
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private OrderService orderService; 
    
    @Autowired 
    private ProductService productService; // Inyección para obtener precios
    
    // Obtiene el carrito del usuario, o crea uno si no existe
    public Cart getOrCreateCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setItems(new ArrayList<>());
            cartRepository.save(cart);
        }
        return cart;
    }

    // Lógica para agregarltem(producto, cantidad): Void 
    public Cart addItem(String userId, String productId, int cantidad) {
        Cart cart = getOrCreateCart(userId);
        
        // Simulación:
        CartItem item = new CartItem();
        item.setProductId(productId);
        item.setCantidad(cantidad);
        item.setSubtotal(100.0 * cantidad); 

        cart.getItems().add(item);
        return cartRepository.save(cart);
    }
    
    // Lógica para actualizar cantidad y subtotal
    public Cart updateItemQuantity(String userId, String productId, int newQuantity) {
        Cart cart = getOrCreateCart(userId);
        
        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Producto no encontrado al actualizar el carrito: " + productId);
        }
        double price = productOpt.get().getPrecio();
        
        boolean updated = false;
        
        for (CartItem item : cart.getItems()) {
            if (item.getProductId().equals(productId)) {
                item.setCantidad(newQuantity);
                item.setSubtotal(price * newQuantity); // RECALCULAR SUBTOTAL
                updated = true;
                break; 
            }
        }
        
        if (updated) {
            return cartRepository.save(cart);
        }
        return cart;
    }
    
    // Lógica para eliminar item del carrito
    public Cart removeItem(String userId, String productId) {
        Cart cart = getOrCreateCart(userId);
        
        if (cart.getItems() != null) {
            cart.getItems().removeIf(item -> item.getProductId().equals(productId));
            return cartRepository.save(cart); 
        }
        return cart; 
    }
    
    // Lógica para vaciarCarrito(): Void 
    public void clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            cart.setItems(new ArrayList<>());
            cartRepository.save(cart);
        }
    }

    // Lógica para checkout(): Pedido 
    public Order checkout(String userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío.");
        }
        
        // 1. Crear el Pedido a partir del Carrito
        Order order = orderService.createOrderFromCart(cart); 
        
        // 2. Vaciar el carrito después de la compra
        clearCart(userId);
        
        // 3. Devolver el Pedido realizado
        return order;
    }
}