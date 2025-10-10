package com.payroll.service;

import com.payroll.entity.User;
import com.payroll.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private InMemoryUserRepository userRepository;
    private SimplePasswordEncoder passwordEncoder;

    // Implementação simples para testes
    static class InMemoryUserRepository implements UserRepository {
        private final Map<Long, User> storage = new HashMap<>();
        private long idSequence = 1;

        @Override
        public User save(User user) {
            if (user.getId() == null) {
                user.setId(idSequence++);
            }
            storage.put(user.getId(), user);
            return user;
        }

        @Override
        public Optional<User> findByUsername(String username) {
            return storage.values().stream()
                    .filter(u -> u.getUsername() != null && u.getUsername().equals(username))
                    .findFirst();
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return storage.values().stream()
                    .filter(u -> u.getEmail() != null && u.getEmail().equals(email))
                    .findFirst();
        }

        @Override
        public List<User> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public boolean existsByUsername(String username) {
            return storage.values().stream()
                    .anyMatch(u -> u.getUsername() != null && u.getUsername().equals(username));
        }

        @Override
        public boolean existsByEmail(String email) {
            return storage.values().stream()
                    .anyMatch(u -> u.getEmail() != null && u.getEmail().equals(email));
        }

        @Override
        public Optional<User> findById(Long id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public void deleteById(Long id) {
            storage.remove(id);
        }
    }

    
    static class SimplePasswordEncoder implements PasswordEncoder {
        @Override
        public String encode(CharSequence rawPassword) {
            return new StringBuilder(rawPassword).reverse().toString();
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return encode(rawPassword).equals(encodedPassword);
        }
    }

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        passwordEncoder = new SimplePasswordEncoder();
        userService = new UserService();
        
        userService.userRepository = userRepository;
        userService.passwordEncoder = passwordEncoder;
    }

    @Test
    void createUser_ShouldEncodePasswordAndSaveUser() {
        User user = new User();
        user.setUsername("user1");
        user.setPassword("senha123");
        Long adminId = 10L;

        User created = userService.createUser(user, adminId);

        assertNotNull(created.getId());
        assertEquals(adminId, created.getCreatedBy());
        assertNotEquals("senha123", created.getPassword());
        assertTrue(passwordEncoder.matches("senha123", created.getPassword()));
    }

    @Test
    void findByUsername_ShouldReturnUser() {
        User user = new User();
        user.setUsername("user2");
        user.setPassword("pwd");
        userService.createUser(user, 1L);

        Optional<User> found = userService.findByUsername("user2");

        assertTrue(found.isPresent());
        assertEquals("user2", found.get().getUsername());
    }

    @Test
    void findByEmail_ShouldReturnUser() {
        User user = new User();
        user.setEmail("email@teste.com");
        user.setUsername("user3");
        userService.createUser(user, 1L);

        Optional<User> found = userService.findByEmail("email@teste.com");

        assertTrue(found.isPresent());
        assertEquals("email@teste.com", found.get().getEmail());
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        User user1 = new User();
        user1.setUsername("u1");
        userService.createUser(user1, 1L);

        User user2 = new User();
        user2.setUsername("u2");
        userService.createUser(user2, 1L);

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void existsByUsername_ShouldReturnTrueIfExists() {
        User user = new User();
        user.setUsername("user4");
        userService.createUser(user, 1L);

        assertTrue(userService.existsByUsername("user4"));
        assertFalse(userService.existsByUsername("nope"));
    }

    @Test
    void existsByEmail_ShouldReturnTrueIfExists() {
        User user = new User();
        user.setEmail("email4@teste.com");
        userService.createUser(user, 1L);

        assertTrue(userService.existsByEmail("email4@teste.com"));
        assertFalse(userService.existsByEmail("noemail@teste.com"));
    }

    @Test
    void validatePassword_ShouldReturnTrueWhenMatch() {
        String raw = "abc123";
        String encoded = passwordEncoder.encode(raw);

        assertTrue(userService.validatePassword(raw, encoded));
        assertFalse(userService.validatePassword("wrong", encoded));
    }

    @Test
    void updateUser_ShouldUpdateFieldsAndEncodePassword() {
        User user = new User();
        user.setUsername("user5");
        user.setPassword("pass5");
        userService.createUser(user, 1L);

        Long id = user.getId();

        User update = new User();
        update.setUsername("newUser5");
        update.setEmail("newemail5@test.com");
        update.setPassword("newpass");
        update.setRole("ADMIN");

        User updated = userService.updateUser(id, update);

        assertEquals("newUser5", updated.getUsername());
        assertEquals("newemail5@test.com", updated.getEmail());
        assertEquals("ADMIN", updated.getRole());
        assertTrue(passwordEncoder.matches("newpass", updated.getPassword()));
    }

    @Test
    void updateUser_ShouldNotChangePasswordIfEmpty() {
        User user = new User();
        user.setUsername("user6");
        user.setPassword("oldpass");
        userService.createUser(user, 1L);

        Long id = user.getId();

        User update = new User();
        update.setUsername("user6new");
        update.setPassword(""); 

        User updated = userService.updateUser(id, update);

        assertEquals("user6new", updated.getUsername());
        assertEquals("oldpass", updated.getPassword()); }

    @Test
    void deleteUser_ShouldRemoveUser() {
        User user = new User();
        user.setUsername("user7");
        userService.createUser(user, 1L);

        Long id = user.getId();
        userService.deleteUser(id);

        assertFalse(userService.findByUsername("user7").isPresent());
    }
}
