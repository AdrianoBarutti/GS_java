package com.stormguard.stormguard_api.controller;

import com.stormguard.stormguard_api.model.User;
import com.stormguard.stormguard_api.repository.UserRepository;
import com.stormguard.stormguard_api.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import io.jsonwebtoken.Claims;


@Tag(name = "Autenticação")
@RestController
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Login de usuário")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        // Busca o usuário no banco
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !user.getPassword().equals(password)) {
            return ResponseEntity.status(401).body("Usuário ou senha inválidos");
        }

        // Gera os tokens JWT
        String token = jwtUtil.generateToken(user.getUsername(), List.of(user.getRole()));
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        return ResponseEntity.ok(Map.of(
            "token", token,
            "refreshToken", refreshToken
        ));
    }

    @Operation(summary = "Registro de usuário")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        String role = user.getRole();
        if (!"ADMIN".equalsIgnoreCase(role) && !"USER".equalsIgnoreCase(role)) {
            return ResponseEntity.badRequest().body("Role deve ser ADMIN ou USER");
        }
        return ResponseEntity.ok(userRepository.save(user));
}

    @Operation(summary = "Refresh token")
    @SuppressWarnings("unchecked")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        Claims claims = jwtUtil.extractClaims(refreshToken);
        String username = claims.getSubject();
        List<String> roles = claims.get("roles", List.class);
        String newAccessToken = jwtUtil.generateToken(username, roles);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }
}