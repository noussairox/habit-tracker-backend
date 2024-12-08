package com.habit_tracker.habitApp.repository;

import com.habit_tracker.habitApp.model.ArchivedHabit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchivedHabitRepository extends JpaRepository<ArchivedHabit, Long> {
}
