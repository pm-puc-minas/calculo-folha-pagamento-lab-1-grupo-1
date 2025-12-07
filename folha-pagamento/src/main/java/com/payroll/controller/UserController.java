package com.payroll.controller;

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
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return ResponseEntity.ok(UserProfileResponseDTO.from(user));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> updateMe(
            @RequestBody UserProfileRequestDTO request,
            Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        User updated = userService.updatePassword(
                authentication.getName(),
                request.getPassword());

        return ResponseEntity.ok(UserProfileResponseDTO.from(updated));
    }
}
