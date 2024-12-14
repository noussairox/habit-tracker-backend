package com.habittacker.habitApp.controller;

import com.habittacker.habitApp.dto.HabitStatisticsDTO;
import com.habittacker.habitApp.dto.ProgressChartDTO;
import com.habittacker.habitApp.model.ArchivedHabit;
import com.habittacker.habitApp.model.Habit;
import com.habittacker.habitApp.repository.ArchivedHabitRepository;
import com.habittacker.habitApp.service.HabitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    @Autowired
    private HabitService habitService;
    
    @Autowired
    private ArchivedHabitRepository archivedHabitRepository;


    @PostMapping
    public Habit createHabit(@RequestBody Habit habit) {
        // Récupérer le nom d'utilisateur authentifié à partir du SecurityContext
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Utilisateur authentifié : " + username);
        return habitService.createHabit(habit);
    }

    @GetMapping("/{id}")
    public Habit getHabitById(@PathVariable Long id) {
        return habitService.getHabitById(id);
    }

    
    @PutMapping("/{id}")
    public Habit updateHabit(@PathVariable Long id, @RequestBody Habit updatedHabit) {
        return habitService.updateHabit(id, updatedHabit);
    }

    
    @PutMapping("/{id}/toggleStatus")
    public Habit toggleHabitStatus(@PathVariable Long id) {
        return habitService.toggleHabitStatus(id);
    }
    

    @PutMapping("/{id}/complete")
    public Habit markHabitAsCompleted(@PathVariable Long id, @RequestBody LocalDate completionDate) {
        return habitService.markHabitAsCompleted(id, completionDate);
    }

    @GetMapping("/filtered")
    public List<Habit> getFilteredHabits(
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String frequency
    ) {
        // Récupérer les habitudes filtrées pour l'utilisateur connecté
        return habitService.getFilteredHabits(isActive, frequency);
    }

    @GetMapping
    public List<Habit> getHabitsForAuthenticatedUser() {
        // Récupérer les habitudes de l'utilisateur authentifié
        return habitService.getHabitsForAuthenticatedUser();
    }

    @DeleteMapping("/{id}")
    public void deleteHabit(@PathVariable Long id) {
        habitService.deleteHabit(id);
    }
    
    @GetMapping("/statistics")
    public HabitStatisticsDTO getHabitStatistics() {
        return habitService.getHabitStatistics();
    }
    
    @GetMapping("/progress-statistics")
    public ProgressChartDTO getProgressStatistics() {
        return habitService.getProgressStatistics();
    }
    @PutMapping("/{id}/update-streak")
    public Habit updateHabitStreak(@PathVariable Long id) {
        return habitService.updateHabitStreak(id);
    }
    
    @GetMapping("/archived")
    public List<ArchivedHabit> getArchivedHabits() {
        return archivedHabitRepository.findAll();
    }

    @PutMapping("/{id}/archive")
    public void archiveHabit(@PathVariable Long id) {
        habitService.checkAndArchiveHabit(id);
    }

    
}
