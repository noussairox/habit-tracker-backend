package com.habittacker.habitapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HabitStatisticsDTO {
    private int totalHabits;
    private int activeHabits;
    private int inactiveHabits;
    private int bestStreak;
    private int archivedHabits;
}
