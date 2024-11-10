package com.habit_tracker.habitApp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.habit_tracker.habitApp.model.User;
import com.habit_tracker.habitApp.repository.UserRepository;
import com.habit_tracker.habitApp.utils.JwtUtils;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@PostMapping("/signup")
	public Map<String, String> registerUser(@RequestBody User user){
		Map<String, String> response = new HashMap<>();
		
		if(userRepository.existsByUsername(user.getUsername())) {
			response.put("message", "Erreur : Le nom d'utilisateur est déjà pris !");
            return response;
		}
		
		if (userRepository.existsByEmail(user.getEmail())) {
            response.put("message", "Erreur : L'email est déjà utilisé !");
            return response;
        }
		// Encoder le mot de passe 
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		// enregistrer l'utilisateur
		userRepository.save(user);
		return response;
	}
	
	@PostMapping("/signin")
    public Map<String, String> authenticateUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        // Vérifier si l'utilisateur existe
        User existingUser = userRepository.findByUsername(user.getUsername())
                .orElse(null);

        if (existingUser == null || !passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            response.put("message", "Erreur : Nom d'utilisateur ou mot de passe incorrect !");
            return response;
        }

        // Générer le token JWT
        String jwt = jwtUtils.generateJwtToken(user.getUsername());
        response.put("token", jwt);
        response.put("message", "Connexion réussie !");
        return response;
    }

}
