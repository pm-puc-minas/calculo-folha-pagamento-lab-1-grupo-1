package com.payroll.repository;

/*
 * Interface de repositório para a entidade User.
 * Gerencia o acesso aos dados de usuários do sistema, fornecendo métodos
 * otimizados para autenticação (busca por login) e validação de cadastro (verificação de existência).
 */

import com.payroll.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar usuário pelo nome de login (utilizado no processo de autenticação/login)
    Optional<User> findByUsername(String username);

    // Buscar usuário pelo e-mail (utilizado para recuperação de conta ou login alternativo)
    Optional<User> findByEmail(String email);

    // Verificar se o nome de usuário já está em uso (validação no registro)
    boolean existsByUsername(String username);

    // Verificar se o e-mail já está cadastrado (validação no registro)
    boolean existsByEmail(String email);
}