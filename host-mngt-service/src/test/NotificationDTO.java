package com.venuex.backend.DTO;

import java.time.LocalDateTime;

public class NotificationDTO {

    private Integer id;
    private String userName;
    private String message;
    private LocalDateTime sentAt;

    public NotificationDTO() {}

    public NotificationDTO(Integer id, String userName, String message, LocalDateTime sentAt) {
        this.id = id;
        this.userName = userName;
        this.message = message;
        this.sentAt = sentAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt (LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}

