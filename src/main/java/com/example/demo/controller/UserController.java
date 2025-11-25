package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.repository.UserRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users") 
public class UserController {

    @Autowired
    private UserService userService; 

    // Se mantiene inyectado para el método GET original del usuario
    @Autowired
    private UserRepository userRepository; 


    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserProfile(@PathVariable String id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/users/{id} - Endpoint para actualizar el perfil del usuario
    @PutMapping("/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable String id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        
        if (updatedUser != null) {
            // Devolvemos el usuario actualizado
            return ResponseEntity.ok(updatedUser);
        } else {
            // Usuario no encontrado
            return ResponseEntity.notFound().build();
        }
    }
}