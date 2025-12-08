package com.payroll.repository;

/*
 * Interface de repositório para operações de autenticação.
 * Estende o JpaRepository para fornecer acesso a dados da entidade User,
 * focando na recuperação de credenciais para validação de login.
 */

import org.springframework.data.jpa.repository.JpaRepository;

import com.payroll.entity.User;

public interface AutenticationRepository extends JpaRepository<User, Long> {

    // Buscar usuário pelo nome de login para validação de credenciais
    User findByUsername(String username);
}