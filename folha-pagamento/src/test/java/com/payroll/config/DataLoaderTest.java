package com.payroll.config;

import com.payroll.entity.User;
import com.payroll.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.any;

class DataLoaderTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private DataLoader dataLoader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa os mocks
    }

    @Test
    void run_createsAdminAndUser_whenTheyDoNotExist() throws Exception {
        // Usuários não existem
        when(userService.existsByUsername("admin")).thenReturn(false);
        when(userService.existsByUsername("user")).thenReturn(false);

        // Executa o DataLoader
        dataLoader.run();

        // Verifica se o usuário admin foi criado
        verify(userService, times(1)).createUser(argThat(user ->
                user.getUsername().equals("admin") &&
                user.getEmail().equals("admin@payroll.com") &&
                user.getRole() == User.Role.ADMIN
        ), isNull());

        // Verifica se o usuário comum foi criado
        verify(userService, times(1)).createUser(argThat(user ->
                user.getUsername().equals("user") &&
                user.getEmail().equals("user@payroll.com") &&
                user.getRole() == User.Role.USER
        ), isNull());
    }

    @Test
    void run_doesNotCreateUsers_whenTheyAlreadyExist() throws Exception {
        // Usuários já existem
        when(userService.existsByUsername("admin")).thenReturn(true);
        when(userService.existsByUsername("user")).thenReturn(true);

        // Executa o DataLoader
        dataLoader.run();

        // Verifica que nenhum usuário foi criado
        verify(userService, never()).createUser(any(User.class), any());
    }
}
