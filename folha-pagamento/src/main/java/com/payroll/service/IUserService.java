package com.payroll.service;

import com.payroll.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * Interface para serviÇõÃ§o de usuÃ¡rios
 */
public interface IUserService {

    User createUser(User user, Long adminId);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> getAllUsers();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean validatePassword(String rawPassword, String encodedPassword);
    User updateUser(Long id, User userDetails);
    void deleteUser(Long id);

    User updateUsernamePassword(String currentUsername, String newUsername, String newPassword);

    /**
     * Atualiza apenas a senha do usuario autenticado.
     * @param currentUsername username atual (do token)
     * @param newPassword nova senha (obrigatoria)
     * @return usuario atualizado
     */
    User updatePassword(String currentUsername, String newPassword);
}
