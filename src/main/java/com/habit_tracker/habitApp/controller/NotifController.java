package com.habit_tracker.habitApp.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.habit_tracker.habitApp.model.Habit;
import com.habit_tracker.habitApp.model.Notification;
import com.habit_tracker.habitApp.repository.HabitRepository;
import com.habit_tracker.habitApp.repository.NotificationRepository;
import com.habit_tracker.habitApp.service.NotificationService;

@RestController
@RequestMapping("api/notif")
public class NotifController {
	@Autowired
    private HabitRepository habitRepository;
	
	@Autowired
	private NotificationRepository notificationRepository;

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
    @GetMapping("/send-test-notifications")
    public String sendTestNotifications() {
        notificationService.sendNotifications();
        return "Notifications de test envoyées avec succès !";
    }
    @GetMapping("/pending")
    public List<Notification> getPendingNotifications() {
        return notificationRepository.findByIsSentFalseAndNotificationTimeBefore(LocalDateTime.now());
    }

    @GetMapping("/recent")
    public List<Notification> getRecentNotifications() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        return notificationRepository.findAll().stream()
                .filter(notification -> notification.getNotificationTime().isAfter(oneDayAgo))
                .collect(Collectors.toList());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
            // Renvoie une réponse JSON valide
            return ResponseEntity.ok(Map.of("message", "Notification supprimée avec succès."));
        } else {
            // Renvoie une réponse JSON valide en cas d'erreur
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Notification non trouvée."));
        }
    }




}
