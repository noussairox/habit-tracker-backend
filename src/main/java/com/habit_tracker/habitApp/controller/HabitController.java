package com.habit_tracker.habitApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.habit_tracker.habitApp.model.Habit;
import com.habit_tracker.habitApp.service.HabitService;

@RestController
@RequestMapping("/api/habits")
public class HabitController {
	
	@Autowired
	private HabitService habitService;
	
	@PostMapping
	public Habit createHabit(@RequestBody Habit habit) {
		return habitService.createHabit(habit);
	}
	
	@GetMapping("/{userId}")
	public List<Habit> getHabitsByUserId(@PathVariable Long userId){
		return habitService.getHabitsByUserId(userId);
	}
	
	@PutMapping("/{id}")
    public Habit updateHabit(@PathVariable Long id, @RequestBody Habit updatedHabit) {
        return habitService.updateHabit(id, updatedHabit);
    }

    @DeleteMapping("/{id}")
    public void deleteHabit(@PathVariable Long id) {
        habitService.deleteHabit(id);
    }
}
