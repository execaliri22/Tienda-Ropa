package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.repository.UserRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/users") 
public class UserController {

    @Autowired
    private UserService userService; 

    @Autowired
    private UserRepository userRepository; 

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserProfile(@PathVariable String id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/users/{id} - Endpoint para actualizar el perfil del usuario (detalles o contraseña)
    @PutMapping("/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable String id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // NUEVO ENDPOINT: POST /api/users/{userId}/profile-picture - Subir foto
    @PostMapping("/{userId}/profile-picture")
    public ResponseEntity<String> uploadProfilePicture(
        @PathVariable String userId,
        @RequestParam("file") MultipartFile file) 
    {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Debe seleccionar un archivo.", HttpStatus.BAD_REQUEST);
        }

        try {
            String relativePath = userService.saveProfilePicture(userId, file.getBytes(), file.getOriginalFilename());
            if (relativePath != null) {
                // Devolver la URL relativa de la nueva foto
                return new ResponseEntity<>(relativePath, HttpStatus.OK); 
            } else {
                return new ResponseEntity<>("Error al guardar la foto de perfil o usuario no encontrado.", HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>("Error al procesar el archivo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // NUEVO ENDPOINT: DELETE /api/users/{userId}/profile-picture - Eliminar foto
    @DeleteMapping("/{userId}/profile-picture")
    public ResponseEntity<String> deleteProfilePicture(@PathVariable String userId) {
        boolean deleted = userService.deleteProfilePicture(userId);

        if (deleted) {
            // Devolver la URL del avatar por defecto para que el frontend la use
            return new ResponseEntity<>("images/default_avatar.png", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Error al eliminar la foto o usuario no encontrado.", HttpStatus.NOT_FOUND);
        }
    }
}