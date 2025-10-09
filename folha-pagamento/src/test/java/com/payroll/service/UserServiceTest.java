package com.payroll.service;

import com.payroll.entity.User;
import com.payroll.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
    }

    @Test
    void testCreateUser() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User created = userService.createUser(user, 99L);

        assertEquals("encodedPassword", created.getPassword());
        assertEquals(99L, created.getCreatedBy());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testFindByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        Optional<User> found = userService.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals(user, found.get());
    }

    @Test
    void testFindByEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        Optional<User> found = userService.findByEmail("test@example.com");
        assertTrue(found.isPresent());
        assertEquals(user, found.get());
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<User> users = userService.getAllUsers();
        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }

    @Test
    void testExistsByUsernameAndEmail() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertTrue(userService.existsByUsername("testuser"));
        assertTrue(userService.existsByEmail("test@example.com"));
    }

    @Test
    void testValidatePassword() {
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        assertTrue(userService.validatePassword("password", "encodedPassword"));
    }

    @Test
    void testUpdateUser() {
        User update = new User();
        update.setUsername("updatedUser");
        update.setEmail("updated@example.com");
        update.setPassword("newpass");
        update.setRole(User.Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass")).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User updated = userService.updateUser(1L, update);

        assertEquals("updatedUser", updated.getUsername());
        assertEquals("updated@example.com", updated.getEmail());
        assertEquals("encodedNewPass", updated.getPassword());
        assertEquals(User.Role.ADMIN, updated.getRole());
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}
