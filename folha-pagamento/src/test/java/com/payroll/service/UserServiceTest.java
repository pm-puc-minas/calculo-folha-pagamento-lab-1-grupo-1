package com.payroll.service;

import com.payroll.entity.User;
import com.payroll.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        // Limpa todos os dados antes de cada teste
        userRepository.deleteAll();

        // Cria um usuário real
        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(User.Role.USER);
    }

    @Test
    void testCreateUser() {
        User created = userService.createUser(user, 1L);

        assertNotNull(created.getId());
        assertEquals(1L, created.getCreatedBy());
        assertNotNull(created.getPassword());
        assertNotEquals("password", created.getPassword()); // senha deve estar hasheada
        assertTrue(passwordEncoder.matches("password", created.getPassword()));
    }

    @Test
    void testFindByUsername() {
        userService.createUser(user, null);

        Optional<User> found = userService.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void testFindByEmail() {
        userService.createUser(user, null);

        Optional<User> found = userService.findByEmail("test@example.com");
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void testGetAllUsers() {
        userService.createUser(user, null);

        List<User> users = userService.getAllUsers();
        assertEquals(1, users.size());
        assertEquals("testuser", users.get(0).getUsername());
    }

    @Test
    void testExistsByUsernameAndEmail() {
        userService.createUser(user, null);

        assertTrue(userService.existsByUsername("testuser"));
        assertTrue(userService.existsByEmail("test@example.com"));
    }

    @Test
    void testValidatePassword() {
        User created = userService.createUser(user, null);

        assertTrue(userService.validatePassword("password", created.getPassword()));
        assertFalse(userService.validatePassword("wrongpass", created.getPassword()));
    }

    @Test
    void testUpdateUser() {
        User created = userService.createUser(user, null);

        User update = new User();
        update.setUsername("updatedUser");
        update.setEmail("updated@example.com");
        update.setPassword("newpass");
        update.setRole(User.Role.ADMIN);

        User updated = userService.updateUser(created.getId(), update);

        assertEquals("updatedUser", updated.getUsername());
        assertEquals("updated@example.com", updated.getEmail());
        assertTrue(userService.validatePassword("newpass", updated.getPassword()));
        assertEquals(User.Role.ADMIN, updated.getRole());
    }

    @Test
void testDeleteUser() {
    User created = userService.createUser(user, null);

    userService.deleteUser(created.getId());

    // Verifica se o usuário foi removido
    assertFalse(userRepository.findById(created.getId()).isPresent());
}

}
