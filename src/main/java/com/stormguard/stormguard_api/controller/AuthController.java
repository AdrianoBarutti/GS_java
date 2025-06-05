package com.stormguard.stormguard_api.controller;

import com.stormguard.stormguard_api.model.User;
import com.stormguard.stormguard_api.repository.UserRepository;
import com.stormguard.stormguard_api.security.JwtUtil;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Operation(summary = "Listar usuários cadastrados")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Login de usuário")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        // Busca o usuário no banco
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body("Usuário ou senha inválidos");
        }

        // Gera os tokens JWT
        String accessToken = jwtUtil.generateToken(user.getUsername(), List.of(user.getRole()));
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        return ResponseEntity.ok(Map.of(
            "accessToken", accessToken,
            "refreshToken", refreshToken
        ));
    }

    @Operation(
        summary = "Registro de usuário",
        description = "Registra um novo usuário com o papel ADMIN ou USER. " +
                      "O papel deve ser especificado no campo 'role' do objeto User."
    )
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        String role = user.getRole();
        if (!"ADMIN".equalsIgnoreCase(role) && !"USER".equalsIgnoreCase(role)) {
            return ResponseEntity.badRequest().body("Role deve ser ADMIN ou USER");
        }

        // Verifica se o usuário já existe
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Usuário já existe");
        }
        // Codifica a senha antes de salvar
        user.setPassword(passwordEncoder.encode(user.getPassword()));

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

    @Operation(summary = "Logout de usuário")
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("Usuário deslogado com sucesso");
    }

    @Operation(summary = "Verifica se o usuário está autenticado")
    @GetMapping("/check")
    public ResponseEntity<?> checkAuthentication(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // Valida o token e extrai as claims
                Claims claims = jwtUtil.extractClaims(token);
                String username = claims.getSubject();

                if (username != null) {
                    return ResponseEntity.ok("Usuário autenticado: " + username);
                }
            } catch (Exception e) {
                return ResponseEntity.status(401).body("Token inválido ou expirado");
            }
        }
        return ResponseEntity.status(401).body("Usuário não autenticado");
    }
}