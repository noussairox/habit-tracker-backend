package com.habittacker.habitapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.habittacker.habitapp.model.Habit;

public interface HabitRepository extends JpaRepository<Habit, Long> {

    List<Habit> findByIsActive(boolean isActive);

    List<Habit> findByUserId(Long userId);
    List<Habit> findByUserIdAndIsActive(Long userId, boolean isActive);
    List<Habit> findByUserIdAndFrequency(Long userId, String frequency);
    List<Habit> findByUserIdAndIsActiveAndFrequency(Long userId, boolean isActive, String frequency);
}
