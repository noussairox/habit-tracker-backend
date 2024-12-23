package com.habittacker.habitapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.habittacker.habitapp.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
	
}
