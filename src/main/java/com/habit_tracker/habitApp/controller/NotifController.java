package com.habit_tracker.habitApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.habit_tracker.habitApp.model.Habit;
import com.habit_tracker.habitApp.repository.HabitRepository;
import com.habit_tracker.habitApp.service.NotificationService;

@RestController
@RequestMapping("api/notif")
public class NotifController {
	@Autowired
    private HabitRepository habitRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/create-notifications")
    public String createTestNotifications() {
        List<Habit> habits = habitRepository.findAll();
        if (habits.isEmpty()) {
            return "Aucune habitude trouvée pour créer des notifications.";
        }
        for (Habit habit : habits) {
            notificationService.createNotificationForHabit(habit);
        }
        return "Notifications créées avec succès pour toutes les habitudes !";
    }
    
}
