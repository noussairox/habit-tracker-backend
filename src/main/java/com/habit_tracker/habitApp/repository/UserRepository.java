package com.habit_tracker.habitApp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.habit_tracker.habitApp.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
}
