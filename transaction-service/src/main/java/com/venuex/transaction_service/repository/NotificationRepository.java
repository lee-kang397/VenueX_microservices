package com.venuex.transaction_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.venuex.transaction_service.entities.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    // ✅ matches: findByUserIdOrderBySentAtDesc(Integer)
    List<Notification> findByUserIdOrderBySentAtDesc(Integer userId);
}
