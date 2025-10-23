package com.payroll.service;

import com.payroll.entity.User;
import com.payroll.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
@Override
    public User createUser(User user, Long adminId) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedBy(adminId);
        return userRepository.save(user);
    }
@Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
@Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
@Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
@Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
@Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
@Override
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
@Override
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id).orElseThrow();
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        user.setRole(userDetails.getRole());
        return userRepository.save(user);
    }
@Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

