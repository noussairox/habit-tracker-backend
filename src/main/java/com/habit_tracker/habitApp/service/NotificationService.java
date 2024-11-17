package com.habit_tracker.habitApp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.habit_tracker.habitApp.model.Habit;
import com.habit_tracker.habitApp.model.Notification;
import com.habit_tracker.habitApp.repository.HabitRepository;
import com.habit_tracker.habitApp.repository.NotificationRepository;

@Service
public class NotificationService {

    // Création d'un logger pour enregistrer des messages de débogage
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private HabitRepository habitRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;

    // Tâche planifiée pour s'exécuter chaque minute
    @Scheduled(cron = "0 0 * * * *")  // Exécution chaque minute pour les tests
    public void sendNotifications() {
        logger.info("Exécution de sendNotifications à : " + LocalDateTime.now());

        // Récupérer les notifications non envoyées dont l'heure est passée
        List<Notification> notifications = notificationRepository.findByIsSentFalseAndNotificationTimeBefore(LocalDateTime.now());
        logger.info("Nombre de notifications à envoyer : " + notifications.size());

        for (Notification notification : notifications) {
            Habit habit = notification.getHabit();

            // Simulez l'envoi de la notification
            logger.info("Envoi de la notification pour l'habitude : " + habit.getName());
            logger.info("Message : " + notification.getMessage());

            // Marquer la notification comme envoyée
            notification.setSent(true);
            notificationRepository.save(notification);

            logger.info("Notification marquée comme envoyée pour : " + habit.getName());
        }
    }

    // Méthode pour créer des rappels pour les habitudes
    public void createNotificationForHabit(Habit habit) {
        logger.info("Création d'une notification pour l'habitude : " + habit.getName());
        LocalDateTime nextNotificationTime = calculateNextNotificationTime(habit);
        logger.info("Heure du prochain rappel : " + nextNotificationTime);

        Notification notification = new Notification();
        notification.setMessage("Rappel : " + habit.getName());
        notification.setNotificationTime(nextNotificationTime);
        notification.setSent(false);
        notification.setHabit(habit);

        // Enregistrer la notification dans la base de données
        notificationRepository.save(notification);
        logger.info("Notification créée et enregistrée pour l'habitude : " + habit.getName());
    }


    // Méthode pour calculer la prochaine heure de rappel en fonction de la fréquence
    private LocalDateTime calculateNextNotificationTime(Habit habit) {
        LocalDateTime now = LocalDateTime.now();
        switch (habit.getFrequency().toLowerCase()) {
            case "quotidien":
                return now.plusMinutes(1);  // Pour tester, envoyer chaque minute
            case "hebdomadaire":
                return now.plusWeeks(1);
            default:
                return now.plusMinutes(1);  // Par défaut, utiliser chaque minute pour les tests
        }
    }
}
