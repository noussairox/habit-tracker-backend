package com.habit_tracker.habitApp.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Habit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private String description;
	
	private String frequency;
	
	//status pour activer/desactiver l'habititude
	private boolean isActive;
	
	@ElementCollection
	private List<LocalDate> completionDates;
	
	private LocalDate startDate;  // Date de début de l'habitude
    private LocalDate endDate;  // Date de fin, si applicable
    private int targetCount;  // Nombre de fois que l'habitude doit être accomplie (par semaine, par mois)
    private int currentStreak;  // Streak actuel pour la gamification
    private int bestStreak;  // Meilleur streak jamais atteint
	
    @ManyToOne
    private User user;
}
