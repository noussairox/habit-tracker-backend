package com.habit_tracker.habitApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.habit_tracker.habitApp.model.User;
import com.habit_tracker.habitApp.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		// Rechercher l'utilisateur par son nom d'utilisateur 
		User user = userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Utilisateur non trouvé"+username));
	
		return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .build();
    }

}
	

