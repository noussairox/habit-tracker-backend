package com.habittacker.habitapp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.habittacker.habitapp.model.Habit;
import com.habittacker.habitapp.model.Notification;
import com.habittacker.habitapp.repository.HabitRepository;
import com.habittacker.habitapp.repository.NotificationRepository;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // Tâche planifiée pour s'exécuter chaque minute
    @Scheduled(cron = "0 */1 * * * *") // Exécution chaque minute pour les tests
    public void sendNotifications() {
        logger.info("Exécution de sendNotifications à : " + LocalDateTime.now());

        // Récupérer les notifications non envoyées dont l'heure est passée
        List<Notification> notifications = notificationRepository.findByIsSentFalseAndNotificationTimeBefore(LocalDateTime.now());
        logger.info("Nombre de notifications à envoyer : " + notifications.size());

        for (Notification notification : notifications) {
            Habit habit = notification.getHabit();

            // Simulez l'envoi de la notification (par ex., par email ou push)
            logger.info("Envoi de la notification pour l'habitude : " + habit.getName());
            logger.info("Message : " + notification.getMessage());

            // Marquer la notification comme envoyée
            notification.setSent(true);
            notificationRepository.save(notification);

            logger.info("Notification marquée comme envoyée pour : " + habit.getName());

            // Créer la prochaine notification pour cette habitude
            createNotificationForHabit(habit);
        }
    }

    // Méthode pour créer des rappels pour les habitudes
    public void createNotificationForHabit(Habit habit) {
        // Vérifier s'il existe déjà une notification non envoyée pour cette habitude
        boolean existingNotification = notificationRepository
            .findByIsSentFalseAndNotificationTimeBefore(LocalDateTime.now())
            .stream()
            .anyMatch(n -> n.getHabit().getId().equals(habit.getId()));

        if (existingNotification) {
            logger.info("Une notification existe déjà pour l'habitude : " + habit.getName());
            return;
        }

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
        switch (habit.getFrequency()) {
            case "quotidienne":
                return now.plusMinutes(1); // Rappel toutes les minutes pour les tests
            case "hebdomadaire":
                return now.plusMinutes(1); // Rappel toutes les minutes pour les tests
            default:
                return now.plusMinutes(1); // Par défaut, rappel toutes les minutes pour les tests
        }
    }

    // Méthode pour créer des notifications initiales pour toutes les habitudes
    public void initializeNotificationsForAllHabits() {
        List<Habit> habits = habitRepository.findAll();
        for (Habit habit : habits) {
            createNotificationForHabit(habit);
        }
        logger.info("Notifications initiales créées pour toutes les habitudes.");
    }
}
