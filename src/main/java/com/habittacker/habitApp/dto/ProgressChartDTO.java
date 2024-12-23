package com.habittacker.habitapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgressChartDTO {
    private int archivedHabits;
    private int remainingHabits;
}
