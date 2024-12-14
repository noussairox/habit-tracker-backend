package com.habittacker.habitApp.repository;

import com.habittacker.habitApp.model.ArchivedHabit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchivedHabitRepository extends JpaRepository<ArchivedHabit, Long> {
}
