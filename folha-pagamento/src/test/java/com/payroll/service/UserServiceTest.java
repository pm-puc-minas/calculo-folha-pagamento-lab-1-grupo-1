package com.payroll.service;

import com.payroll.entity.User;
import com.payroll.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Cria usuário com senha hasheada e createdBy")
    // Verifica criação de usuário com senha hasheada e auditoria createdBy
    void deveCriarUsuarioComSenhaHasheadaECreatedBy() {
        User created = userService.createUser(user, 1L);

        assertNotNull(created.getId());
        assertEquals(1L, created.getCreatedBy());
        assertNotNull(created.getPassword());
        assertNotEquals("password", created.getPassword()); // senha deve estar hasheada
        assertTrue(passwordEncoder.matches("password", created.getPassword()));
    }

    @Test
    @DisplayName("Encontra usuário por username")
    // Verifica busca por username
    void deveEncontrarUsuarioPorUsername() {
        userService.createUser(user, null);

        Optional<User> found = userService.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    @DisplayName("Encontra usuário por email")
    // Verifica busca por email
    void deveEncontrarUsuarioPorEmail() {
        userService.createUser(user, null);

        Optional<User> found = userService.findByEmail("test@example.com");
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Lista todos usuários")
    // Verifica listagem de todos os usuários
    void deveListarTodosUsuarios() {
        userService.createUser(user, null);

        List<User> users = userService.getAllUsers();
        assertEquals(1, users.size());
        assertEquals("testuser", users.get(0).getUsername());
    }

    @Test
    @DisplayName("Verifica existência por username e email")
    // Confirma que existe por username e email
    void deveVerificarExistenciaPorUsernameEEmail() {
        userService.createUser(user, null);

        assertTrue(userService.existsByUsername("testuser"));
        assertTrue(userService.existsByEmail("test@example.com"));
    }

    @Test
    @DisplayName("Valida senha correta e incorreta")
    // Confere validação de senha correta e incorreta
    void deveValidarSenhaCorretaEIncorreta() {
        User created = userService.createUser(user, null);

        assertTrue(userService.validatePassword("password", created.getPassword()));
        assertFalse(userService.validatePassword("wrongpass", created.getPassword()));
    }

    @Test
    @DisplayName("Atualiza usuário persistindo alterações")
    // Verifica atualização e persistência das alterações do usuário
    void deveAtualizarUsuarioPersistindoAlteracoes() {
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
    @DisplayName("Deleta usuário com sucesso")
    // Verifica deleção de usuário e remoção do repositório
    void deveDeletarUsuarioComSucesso() {
        User created = userService.createUser(user, null);

        userService.deleteUser(created.getId());

        // Verifica se o usuário foi removido
        assertFalse(userRepository.findById(created.getId()).isPresent());
    }

}
