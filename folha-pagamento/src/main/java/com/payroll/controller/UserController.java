package com.payroll.controller;

/*
 * Controlador REST para gerenciamento de usuários.
 * Disponibiliza endpoints para consulta e atualização
 * dos dados do próprio usuário autenticado no contexto de segurança.
 */

import com.payroll.dtos.user.UserProfileRequestDTO;
import com.payroll.dtos.user.UserProfileResponseDTO;
import com.payroll.entity.User;
import com.payroll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> me(Authentication authentication) {
        // Verificar se há uma autenticação válida no contexto
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        // Buscar e recuperar o usuário atual baseando-se no token
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Retornar os dados do perfil formatados via DTO
        return ResponseEntity.ok(UserProfileResponseDTO.from(user));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> updateMe(
            @RequestBody UserProfileRequestDTO request,
            Authentication authentication) {
        // Garantir que a requisição provém de um usuário autenticado
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        // Executar a lógica de atualização de senha no serviço
        User updated = userService.updatePassword(
                authentication.getName(),
                request.getPassword());

        // Retornar o perfil com os dados atualizados
        return ResponseEntity.ok(UserProfileResponseDTO.from(updated));
    }
}