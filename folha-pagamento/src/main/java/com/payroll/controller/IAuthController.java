package com.payroll.controller;

import org.springframework.http.ResponseEntity;
import java.util.Map;

public interface IAuthController {
    ResponseEntity<?> login(Map<String, String> loginRequest);
    ResponseEntity<?> refresh(Map<String, String> request);
    ResponseEntity<?> register(Map<String, String> request);
}