package com.payroll.service;

/*
 * Serviço personalizado de detalhes do usuário para o Spring Security.
 * Implementa a interface padrão para carregar dados de autenticação do banco,
 * permitindo login flexível via nome de usuário ou e-mail e mapeando permissões.
 */

import com.payroll.entity.User;
import com.payroll.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Estratégia de busca híbrida: tenta encontrar pelo username, senão tenta pelo e-mail
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + username)));

        // Converter a entidade do banco (Domain User) para o objeto de sessão do Spring (UserDetails)
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(getAuthorities(user))
                .build();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        // Converter o perfil de acesso (Role Enum) para uma Autoridade do Spring (prefixo ROLE_)
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }
}