package com.habitracker.habitapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitracker.habitapp.TestSecurityConfig;
import com.habittacker.habitapp.HabitAppApplication;
import com.habittacker.habitapp.dto.HabitStatisticsDTO;
import com.habittacker.habitapp.model.Habit;
import com.habittacker.habitapp.service.HabitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {HabitAppApplication.class, TestSecurityConfig.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for testing
class HabitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HabitService habitService;

    @BeforeEach
    void setUp() {
        // Mock SecurityContext
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser"); // Mock authenticated username

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext); // Set the mock SecurityContext
    }

    @Test
    void testCreateHabit() throws Exception {
        // Arrange
        Habit habit = new Habit();
        habit.setId(1L);
        habit.setName("Workout");
        habit.setDescription("Daily workout routine");

        when(habitService.createHabit(any(Habit.class))).thenReturn(habit);

        // Act & Assert
        mockMvc.perform(post("/api/habits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(habit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Workout"))
                .andExpect(jsonPath("$.description").value("Daily workout routine"));
    }

    @Test
    void testGetHabitById() throws Exception {
        // Arrange
        Habit habit = new Habit();
        habit.setId(1L);
        habit.setName("Read");
        habit.setDescription("Read a book");

        when(habitService.getHabitById(1L)).thenReturn(habit);

        // Act & Assert
        mockMvc.perform(get("/api/habits/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Read"))
                .andExpect(jsonPath("$.description").value("Read a book"));
    }

    @Test
    void testUpdateHabit() throws Exception {
        // Arrange
        Habit updatedHabit = new Habit();
        updatedHabit.setId(1L);
        updatedHabit.setName("Updated Habit");
        updatedHabit.setDescription("Updated description");

        when(habitService.updateHabit(Mockito.eq(1L), any(Habit.class))).thenReturn(updatedHabit);

        // Act & Assert
        mockMvc.perform(put("/api/habits/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedHabit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Habit"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void testDeleteHabit() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/habits/1"))
                .andExpect(status().isOk());

        Mockito.verify(habitService, Mockito.times(1)).deleteHabit(1L);
    }

    @Test
    void testGetHabitStatistics() throws Exception {
        // Arrange
        HabitStatisticsDTO statistics = new HabitStatisticsDTO(10, 5, 5, 7, 2);

        when(habitService.getHabitStatistics()).thenReturn(statistics);

        // Act & Assert
        mockMvc.perform(get("/api/habits/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalHabits").value(10))
                .andExpect(jsonPath("$.activeHabits").value(5))
                .andExpect(jsonPath("$.inactiveHabits").value(5))
                .andExpect(jsonPath("$.bestStreak").value(7))
                .andExpect(jsonPath("$.archivedHabits").value(2));
    }

    @Test
    void testGetFilteredHabits() throws Exception {
        // Arrange
        Habit habit = new Habit();
        habit.setId(1L);
        habit.setName("Meditation");
        habit.setDescription("Daily meditation");

        when(habitService.getFilteredHabits(true, "daily"))
                .thenReturn(Collections.singletonList(habit));

        // Act & Assert
        mockMvc.perform(get("/api/habits/filtered")
                        .param("isActive", "true")
                        .param("frequency", "daily"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Meditation"))
                .andExpect(jsonPath("$[0].description").value("Daily meditation"));
    }
}
