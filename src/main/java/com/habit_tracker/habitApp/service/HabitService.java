package com.habit_tracker.habitApp.service;

import com.habit_tracker.habitApp.dto.HabitStatisticsDTO;
import com.habit_tracker.habitApp.dto.ProgressChartDTO;
import com.habit_tracker.habitApp.model.Habit;
import com.habit_tracker.habitApp.model.User;
import com.habit_tracker.habitApp.repository.HabitRepository;
import com.habit_tracker.habitApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class HabitService {

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Créer une habitude associée à l'utilisateur authentifié
     */
    public Habit createHabit(Habit habit) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        habit.setUser(user);
        return habitRepository.save(habit);
    }

    /**
     * Mettre à jour une habitude existante
     */
    public Habit updateHabit(Long id, Habit updatedHabit) {
        Habit habit = habitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habit not found"));
        habit.setName(updatedHabit.getName());
        habit.setDescription(updatedHabit.getDescription());
        habit.setFrequency(updatedHabit.getFrequency());
        habit.setTargetCount(updatedHabit.getTargetCount());
        habit.setActive(updatedHabit.isActive());
        return habitRepository.save(habit);
    }

    /**
     * Activer ou désactiver une habitude
     */
    public Habit toggleHabitStatus(Long id) {
        Habit habit = habitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habit not found"));
        habit.setActive(!habit.isActive());
        return habitRepository.save(habit);
    }

    /**
     * Supprimer une habitude
     */
    public void deleteHabit(Long id) {
        habitRepository.deleteById(id);
    }

    /**
     * Marquer une habitude comme complétée pour une date
     */
    public Habit markHabitAsCompleted(Long habitId, LocalDate completionDate) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));
        if (!habit.getCompletionDates().contains(completionDate)) {
            habit.getCompletionDates().add(completionDate);
        }
        return habitRepository.save(habit);
    }

    /**
     * Récupérer les habitudes de l'utilisateur authentifié
     */
    public List<Habit> getHabitsForAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return habitRepository.findByUserId(user.getId());
    }

    /**
     * Récupérer les habitudes filtrées pour un utilisateur donné
     */
    public List<Habit> getFilteredHabits(Boolean isActive, String frequency) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (isActive != null && frequency != null) {
            return habitRepository.findByUserIdAndIsActiveAndFrequency(user.getId(), isActive, frequency);
        } else if (isActive != null) {
            return habitRepository.findByUserIdAndIsActive(user.getId(), isActive);
        } else if (frequency != null) {
            return habitRepository.findByUserIdAndFrequency(user.getId(), frequency);
        }
        return habitRepository.findByUserId(user.getId());
    }

    public Habit getHabitById(Long id) {
        return habitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habit not found with ID: " + id));
    }
    
    public HabitStatisticsDTO getHabitStatistics() {
        // Récupérer l'utilisateur connecté
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupérer les habitudes de l'utilisateur
        List<Habit> habits = habitRepository.findByUserId(user.getId());

        // Calcul des statistiques
        int totalHabits = habits.size();
        int activeHabits = (int) habits.stream().filter(Habit::isActive).count();
        int inactiveHabits = totalHabits - activeHabits;
        int bestStreak = habits.stream()
                .mapToInt(Habit::getBestStreak)
                .max()
                .orElse(0); // Valeur par défaut si aucune habitude n'existe

        return new HabitStatisticsDTO(totalHabits, activeHabits, inactiveHabits, bestStreak);
    }

    /**
     * Calculer les statistiques de progression pour l'utilisateur connecté
     */
    public ProgressChartDTO getProgressStatistics() {
        // Récupérer l'utilisateur connecté
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupérer toutes les habitudes de l'utilisateur
        List<Habit> habits = habitRepository.findByUserId(user.getId());

        // Calculer les habitudes complétées et restantes
        int completedHabits = (int) habits.stream()
                .filter(habit -> habit.getCompletionDates().size() >= habit.getTargetCount())
                .count();
        int remainingHabits = habits.size() - completedHabits;

        // Retourner les statistiques
        return new ProgressChartDTO(completedHabits, remainingHabits);
    }
}
