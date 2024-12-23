package com.habittacker.habitapp.repository;

import com.habittacker.habitapp.model.ArchivedHabit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchivedHabitRepository extends JpaRepository<ArchivedHabit, Long> {
}
