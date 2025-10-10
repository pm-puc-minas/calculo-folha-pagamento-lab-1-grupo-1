package com.payroll.service;

import com.payroll.entity.User;
import com.payroll.entity.Employee;
import com.payroll.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsServiceTest {

    private CustomUserDetailsService userDetailsService;
    private FakeUserRepository fakeUserRepository;


    static class FakeUserRepository implements UserRepository {
        private User storedUser;

        void save(User user) {
            this.storedUser = user;
        }

        @Override
        public Optional<User> findByUsername(String username) {
            if (storedUser != null && storedUser.getUsername().equals(username)) {
                return Optional.of(storedUser);
            }
            return Optional.empty();
        }

    
    }

    @BeforeEach
    void setup() {
        fakeUserRepository = new FakeUserRepository();
        userDetailsService = new CustomUserDetailsService();
       
        userDetailsService.userRepository = fakeUserRepository;

        // Criando um usuário de exemplo
        User user = new User();
        user.setId(1L);
        user.setUsername("joao");
        user.setPassword("senha123");
        user.setRole(User.Role.ADMIN);

        // Aqui criamos um employee para ilustrar e associar caso se quiser
        Employee employee = new Employee();
        employee.setNome("João Silva");
        employee.setCpf("12345678900");
        user.setEmployee(employee);

        fakeUserRepository.save(user);
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("joao");

        assertNotNull(userDetails);
        assertEquals("joao", userDetails.getUsername());
        assertEquals("senha123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("usuarioInexistente");
        });
    }
}
