package com.habittacker.habitApp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.habittacker.habitApp.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>{

	List<Notification> findByIsSentFalseAndNotificationTimeBefore(LocalDateTime time);
}
