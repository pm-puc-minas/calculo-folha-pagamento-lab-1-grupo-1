package com.payroll.service;

/*
 * Serviço de gerenciamento de usuários e segurança.
 * Responsável pelo ciclo de vida das contas de acesso (CRUD),
 * garantindo a criptografia de senhas e validações de unicidade de login.
 */

import com.payroll.entity.User;
import com.payroll.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user, Long adminId) {
        // Criptografar a senha (hash) antes de persistir no banco para segurança
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedBy(adminId);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        // Comparar senha em texto plano com o hash armazenado usando o algoritmo de segurança
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id).orElseThrow();
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        
        // Re-criptografar senha apenas se ela foi alterada na edição
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        
        user.setRole(userDetails.getRole());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User updateUsernamePassword(String currentUsername, String newUsername, String newPassword) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Validar conflito de nome de usuário (evitar duplicidade)
        if (newUsername != null && !newUsername.isBlank() && !newUsername.equals(user.getUsername())) {
            if (userRepository.existsByUsername(newUsername)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already in use");
            }
            user.setUsername(newUsername);
        }

        // Atualizar senha se fornecida
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        return userRepository.save(user);
    }

    @Override
    public User updatePassword(String currentUsername, String newPassword) {
        // Método dedicado para rotação de senha (ex: "Esqueci minha senha" ou troca simples)
        if (newPassword == null || newPassword.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nova senha obrigatoria");
        }
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}