package com.habit_tracker.habitApp.controller;

import com.habit_tracker.habitApp.model.User;
import com.habit_tracker.habitApp.service.AuthService;
import com.habit_tracker.habitApp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (authService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Erreur : Ce nom d'utilisateur est déjà pris !");
        }
        if (authService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Erreur : Cet email est déjà utilisé !");
        }

        // Encoder le mot de passe
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        authService.saveUser(user);

        return ResponseEntity.ok("Utilisateur enregistré avec succès !");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody User user) {
        User existingUser = authService.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Nom d'utilisateur non trouvé !"));

        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            return ResponseEntity.badRequest().body("Erreur : Mot de passe incorrect !");
        }

        // Générer le token JWT
        String token = jwtUtils.generateToken(existingUser.getUsername());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("message", "Connexion réussie !");
        return ResponseEntity.ok(response);
    }
}
