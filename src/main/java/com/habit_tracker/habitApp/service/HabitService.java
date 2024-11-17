package com.habit_tracker.habitApp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.habit_tracker.habitApp.model.Habit;
import com.habit_tracker.habitApp.repository.HabitRepository;

@Service
public class HabitService {
	
	@Autowired
	private HabitRepository habitRepository;
	
	public Habit createHabit(Habit habit) {
		habit.setCurrentStreak(0);
		return habitRepository.save(habit);
	}
	public List<Habit> getHabitsByUserId(Long userId) {
        return habitRepository.findByUserId(userId);
    }
	
	public Habit updateHabit(Long id, Habit updatedHabit) {
        Habit habit = habitRepository.findById(id).orElseThrow(() -> new RuntimeException("Habit not found"));
        habit.setName(updatedHabit.getName());
        habit.setDescription(updatedHabit.getDescription());
        habit.setFrequency(updatedHabit.getFrequency());
        habit.setTargetCount(updatedHabit.getTargetCount());
        habit.setActive(updatedHabit.isActive());
        return habitRepository.save(habit);
    }

    public void deleteHabit(Long id) {
        habitRepository.deleteById(id);
    }
}
