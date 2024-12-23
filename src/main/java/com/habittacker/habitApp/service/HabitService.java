package com.habittacker.habitapp.service;

import com.habittacker.habitapp.dto.HabitStatisticsDTO;
import com.habittacker.habitapp.dto.ProgressChartDTO;
import com.habittacker.habitapp.model.ArchivedHabit;
import com.habittacker.habitapp.model.Habit;
import com.habittacker.habitapp.model.User;
import com.habittacker.habitapp.repository.ArchivedHabitRepository;
import com.habittacker.habitapp.repository.HabitRepository;
import com.habittacker.habitapp.repository.UserRepository;
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

    @Autowired
    private ArchivedHabitRepository archivedHabitRepository;
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
        
     // Calcul du total des habitudes archivées
        int archivedHabits = archivedHabitRepository.findAll().size();
        
        return new HabitStatisticsDTO(totalHabits, activeHabits, inactiveHabits, bestStreak,archivedHabits);
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
        int archivedHabits = archivedHabitRepository.findAll().size();

        // Calculer les habitudes restantes (actives + inactives)
        int totalHabits = habits.size();
        int remainingHabits = totalHabits;

        // Retourner les statistiques avec les archivées
        return new ProgressChartDTO(archivedHabits, remainingHabits);
    }

    
    public Habit updateHabitStreak(Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        // Mettre à jour le streak actuel et le meilleur streak
        habit.setCurrentStreak(habit.getCurrentStreak() + 1);
        if (habit.getCurrentStreak() > habit.getBestStreak()) {
            habit.setBestStreak(habit.getCurrentStreak());
        }

        habit = habitRepository.save(habit);

        // Vérifier et archiver l'habitude si elle atteint son objectif
        checkAndArchiveHabit(habitId);

        return habit;
    }

    
    public void checkAndArchiveHabit(Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        System.out.println("Vérification pour archivage de l'habitude : " + habit.getName());
        System.out.println("Streak Actuel : " + habit.getCurrentStreak());
        System.out.println("Objectif : " + habit.getTargetCount());

        // Vérifiez si le streak actuel atteint ou dépasse l'objectif
        if (habit.getCurrentStreak() >= habit.getTargetCount()) {
            System.out.println("Archiver l'habitude : " + habit.getName());

            // Créer une copie de l'habitude dans ArchivedHabit
            ArchivedHabit archivedHabit = new ArchivedHabit(
                    null, // L'ID sera généré automatiquement
                    habit.getName(),
                    habit.getDescription(),
                    habit.getFrequency(),
                    habit.getStartDate(),
                    habit.getEndDate(),
                    habit.getTargetCount(),
                    habit.getBestStreak(),
                    LocalDate.now() // Date d'archivage
            );
            archivedHabitRepository.save(archivedHabit);

            // Supprimez l'habitude de la table des habitudes actives
            habitRepository.delete(habit);
            System.out.println("Habitude archivée et supprimée des actives.");
        } else {
            System.out.println("L'habitude n'a pas encore atteint son objectif.");
        }
    }

    
    

}
