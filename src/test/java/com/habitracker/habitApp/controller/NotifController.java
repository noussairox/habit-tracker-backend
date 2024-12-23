package com.habitracker.habitapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitracker.habitapp.TestSecurityConfig;
import com.habittacker.habitapp.HabitAppApplication;
import com.habittacker.habitapp.model.Habit;
import com.habittacker.habitapp.model.Notification;
import com.habittacker.habitapp.repository.HabitRepository;
import com.habittacker.habitapp.repository.NotificationRepository;
import com.habittacker.habitapp.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {HabitAppApplication.class, TestSecurityConfig.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class NotifControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HabitRepository habitRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private NotificationService notificationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateTestNotifications() throws Exception {
        // Arrange
        Habit habit = new Habit();
        habit.setId(1L);
        habit.setName("Exercise");
        when(habitRepository.findAll()).thenReturn(Collections.singletonList(habit));

        // Act & Assert
        mockMvc.perform(get("/api/notif/create-notifications"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notifications créées avec succès pour toutes les habitudes !"));

        verify(notificationService, times(1)).createNotificationForHabit(habit);
    }

    @Test
    void testCreateTestNotificationsNoHabits() throws Exception {
        // Arrange
        when(habitRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/notif/create-notifications"))
                .andExpect(status().isOk())
                .andExpect(content().string("Aucune habitude trouvée pour créer des notifications."));

        verify(notificationService, never()).createNotificationForHabit(any());
    }

    @Test
    void testSendTestNotifications() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/notif/send-test-notifications"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notifications de test envoyées avec succès !"));

        verify(notificationService, times(1)).sendNotifications();
    }

    @Test
    void testGetPendingNotifications() throws Exception {
        // Arrange
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setMessage("Pending Notification");
        notification.setNotificationTime(LocalDateTime.now().minusHours(1));
        notification.setSent(false);

        when(notificationRepository.findByIsSentFalseAndNotificationTimeBefore(any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(notification));

        // Act & Assert
        mockMvc.perform(get("/api/notif/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].message").value("Pending Notification"));
    }

    @Test
    void testGetRecentNotifications() throws Exception {
        // Arrange
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setMessage("Recent Notification");
        notification.setNotificationTime(LocalDateTime.now());

        when(notificationRepository.findAll()).thenReturn(Collections.singletonList(notification));

        // Act & Assert
        mockMvc.perform(get("/api/notif/recent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].message").value("Recent Notification"));
    }

    @Test
    void testDeleteNotification() throws Exception {
        // Arrange
        when(notificationRepository.existsById(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/notif/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Notification supprimée avec succès."));

        verify(notificationRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteNotificationNotFound() throws Exception {
        // Arrange
        when(notificationRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/notif/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Notification non trouvée."));

        verify(notificationRepository, never()).deleteById(anyLong());
    }
}
