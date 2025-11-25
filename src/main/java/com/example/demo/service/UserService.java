package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UserService {

    // DIRECTORIO DE ALMACENAMIENTO DE FOTOS
    // Apunta a src/main/resources/static/uploads/profile_pictures
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/profile_pictures/";
    private final String DEFAULT_AVATAR = "images/default_avatar.png"; // Avatar por defecto
    
    // Nota: Necesitas crear manualmente la carpeta: src/main/resources/static/uploads/profile_pictures/
    // Y añadir una imagen 'images/default_avatar.png' en static/images/

    @Autowired
    private UserRepository userRepository;
    
    public UserService() {
        // Asegurar que el directorio de subida existe
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (Exception e) {
            System.err.println("No se pudo crear el directorio de subidas: " + e.getMessage());
        }
    }

    public String register(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "El usuario ya existe";
        }
        user.setProfilePictureUrl(DEFAULT_AVATAR); // Establecer avatar por defecto
        userRepository.save(user);
        return "Usuario registrado exitosamente";
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        
        if (user != null && user.getPassword().equals(password)) {
            // Asegurar que la URL de la foto está configurada, si es nula, usar el default
            if (user.getProfilePictureUrl() == null) {
                user.setProfilePictureUrl(DEFAULT_AVATAR);
                userRepository.save(user);
            }
            return user;
        }
        return null;
    }

    /**
     * Actualiza los datos de un usuario por su ID (nombre/dirección/contraseña).
     */
    public User updateUser(String userId, User updatedUser) {
        return userRepository.findById(userId).map(user -> {
            if (updatedUser.getNombre() != null) {
                user.setNombre(updatedUser.getNombre());
            }
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(updatedUser.getPassword());
            }
            if (updatedUser.getDireccion() != null) {
                user.setDireccion(updatedUser.getDireccion());
            }
            // NO ACTUALIZAR profilePictureUrl aquí
            
            return userRepository.save(user);
        }).orElse(null);
    }
    
    /**
     * Sube y establece la foto de perfil del usuario.
     */
    public String saveProfilePicture(String userId, byte[] fileBytes, String originalFilename) {
        return userRepository.findById(userId).map(user -> {
            try {
                // 1. Crear un nombre de archivo único
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                // El nombre de archivo se basa en el ID del usuario
                String newFileName = userId + "_" + System.currentTimeMillis() + fileExtension;
                Path filePath = Paths.get(UPLOAD_DIR + newFileName);

                // 2. Escribir el archivo en el sistema de archivos
                Files.write(filePath, fileBytes);
                
                // 3. Eliminar la foto anterior si no es la por defecto
                deletePreviousProfilePicture(user);

                // 4. Actualizar el modelo del usuario
                String relativePath = "uploads/profile_pictures/" + newFileName;
                user.setProfilePictureUrl(relativePath);
                userRepository.save(user);

                return relativePath; // Devolver la ruta para actualizar el frontend
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).orElse(null);
    }
    
    /**
     * Elimina la foto de perfil del sistema de archivos y restablece la URL a la por defecto.
     */
    public boolean deleteProfilePicture(String userId) {
        return userRepository.findById(userId).map(user -> {
            
            deletePreviousProfilePicture(user);
            
            // Restablecer la URL del usuario a la por defecto
            user.setProfilePictureUrl(DEFAULT_AVATAR);
            userRepository.save(user);
            return true;
        }).orElse(false);
    }
    
    private void deletePreviousProfilePicture(User user) {
         String currentPath = user.getProfilePictureUrl();
         // 1. Eliminar la foto anterior del sistema de archivos si existe y no es la por defecto
         if (currentPath != null && !currentPath.equals(DEFAULT_AVATAR)) {
            try {
                // Se construye la ruta completa
                Path fullPath = Paths.get("src/main/resources/static/" + currentPath);
                if (Files.exists(fullPath)) {
                    Files.delete(fullPath);
                }
            } catch (Exception e) {
                System.err.println("Error al eliminar la foto anterior: " + e.getMessage());
            }
        }
    }
}