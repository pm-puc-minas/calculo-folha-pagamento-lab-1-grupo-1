package com.payroll.repository;

/*
 * Interface base genérica para os repositórios do sistema.
 * Serve como um contrato intermediário para centralizar configurações ou métodos
 * comuns a todas as entidades, padronizando o acesso a dados.
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

// Instrução para o Spring Data não tentar instanciar esta interface diretamente
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {}