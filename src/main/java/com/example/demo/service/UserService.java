package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public String register(User user) {
        // Verificar si el usuario ya existe
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "El usuario ya existe";
        }
        
        // Guardar el nuevo usuario
        userRepository.save(user);
        return "Usuario registrado exitosamente";
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        
        if (user != null && user.getPassword().equals(password)) {
            return user; // Devuelve el objeto usuario completo
        }
        
        return null; // Credenciales incorrectas
    }

    /**
     * Actualiza los datos de un usuario por su ID.
     * @param userId El ID del usuario a modificar.
     * @param updatedUser El objeto User con los nuevos datos.
     * @return El usuario actualizado o null si no se encuentra.
     */
    public User updateUser(String userId, User updatedUser) {
        return userRepository.findById(userId).map(user -> {
            // Actualizar solo los campos que pueden ser modificados
            if (updatedUser.getNombre() != null) {
                user.setNombre(updatedUser.getNombre());
            }
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                // En una app real, aquí encriptarías la nueva contraseña
                user.setPassword(updatedUser.getPassword());
            }
            if (updatedUser.getDireccion() != null) {
                user.setDireccion(updatedUser.getDireccion());
            }
            // El email (identificador único) no se permite cambiar a través de esta función simple
            
            return userRepository.save(user);
        }).orElse(null);
    }
}