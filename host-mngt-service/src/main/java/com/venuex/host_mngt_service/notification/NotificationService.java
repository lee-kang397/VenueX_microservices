package com.venuex.host_mngt_service.notification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.venuex.host_mngt_service.user.User;
import com.venuex.host_mngt_service.user.UserRepository;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public NotificationDTO mapToDTO(Notification notification) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(notification.getId());
        notificationDTO.setUserName(notification.getUser().getFirstName());
        notificationDTO.setMessage(notification.getMessage());
        notificationDTO.setSentAt(notification.getSentAt());
        
        return notificationDTO;
    }

    public List<NotificationDTO> getUserNotification(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
            List<Notification> notifications = notificationRepository.findByUserIdOrderBySentAtDesc(user.getId());
            List<NotificationDTO> returnDTO = new ArrayList<>();

        for (Notification notification : notifications) {
            returnDTO.add(mapToDTO(notification));
        }
        return returnDTO;
    }

    public Notification createNotification(User user, String message) {
        if (user == null)  {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (message == null || message.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Message cannot be empty");
        }
        Notification notification = new Notification(user,Notification.EmailType.EMAIL,message);
        return notificationRepository.save(notification);
    }

    public void deleteNotification(Integer id, String userEmail) {
        Notification existingNotification = notificationRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

        if (!userEmail.equals(existingNotification.getUser().getEmail())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authorized to delete");
        }
        notificationRepository.delete(existingNotification);
    }

}
