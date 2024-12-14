package com.habittacker.habitApp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArchivedHabit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String frequency;

    private LocalDate startDate;

    private LocalDate endDate;

    private int targetCount;

    private int bestStreak;

    private LocalDate completionDate; // Date à laquelle l'habitude a été terminée
}
