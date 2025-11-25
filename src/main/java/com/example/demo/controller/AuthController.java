package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User authenticatedUser = userService.login(user.getEmail(), user.getPassword());
        
        if (authenticatedUser != null) {
            // Devolver información del usuario en formato JSON
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("id", authenticatedUser.getId()); // ID del usuario agregado
            response.put("name", authenticatedUser.getNombre());
            response.put("email", authenticatedUser.getEmail());
            response.put("message", "Login exitoso");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Usuario o contraseña incorrecta");
            return ResponseEntity.status(401).body(response);
        }
    }
}