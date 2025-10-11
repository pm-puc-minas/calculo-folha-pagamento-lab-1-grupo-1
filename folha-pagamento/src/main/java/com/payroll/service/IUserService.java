package com.payroll.service;

import com.payroll.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * Interface para serviço de usuários
 */
public interface IUserService {
    
    /**
     * Cria novo usuário
     * @param user Dados do usuário
     * @param adminId ID do admin que criou
     * @return Usuário criado
     */
    User createUser(User user, Long adminId);
    
    /**
     * Busca usuário por username
     * @param username Username a buscar
     * @return Optional com usuário
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Busca usuário por email
     * @param email Email a ser buscado
     * @return Optional com usuário
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Lista todos os usuários
     * @return listagem dos usuários
     */
    List<User> getAllUsers();
    
    /**
     * Verifica se username já existe
     * @param username Username a verificar
     * @return true se existe
     */
    boolean existsByUsername(String username);
    
    /**
     * Verifica se tememail 
     * @param email Email a verificacao
     * @return true se tem email
     */
    boolean existsByEmail(String email);
    
    /**
     * Valida senha
     * @param rawPassword Senha 
     * @param encodedPassword Senha codificada
     * @return true se senha for realmente válida
     */
    boolean validatePassword(String rawPassword, String encodedPassword);
    
    /**
     * att usuário
     * @param id id usuario pra att
     * @param userDetails Dados atualizados
     * @return Usuário att
     */
    User updateUser(Long id, User userDetails);
    
    /**
     * Deleta usuário
     * @param id id usuario pra delete
     */
    void deleteUser(Long id);
}