package com.venuex.host_mngt_service.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer>{

    List<Notification> findByUserIdOrderBySentAtDesc(Integer userId);

}
