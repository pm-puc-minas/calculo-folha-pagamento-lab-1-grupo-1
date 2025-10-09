package com.payroll.config;

import com.payroll.entity.User;
import com.payroll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // Criar usuário administrador padrão
        if (!userService.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@payroll.com");
            admin.setPassword("admin123");
            admin.setRole(User.Role.ADMIN);
            userService.createUser(admin, null);
            System.out.println("✅ Usuário administrador criado: admin/admin123");
        }

        // Criar usuário comum padrão
        if (!userService.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@payroll.com");
            user.setPassword("user123");
            user.setRole(User.Role.USER);
            userService.createUser(user, null);
            System.out.println("✅ Usuário comum criado: user/user123");
        }
    }
}