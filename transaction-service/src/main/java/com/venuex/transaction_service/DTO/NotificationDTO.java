package com.venuex.transaction_service.DTO;

import java.time.LocalDateTime;

public class NotificationDTO {

    private Integer id;
    private String name;
    private String message;
    private LocalDateTime sentAt;

    public NotificationDTO() {
    }

    public NotificationDTO(Integer id, String name, String message, LocalDateTime sentAt) {
        this.id = id;
        this.name = name;
        this.message = message;
        this.sentAt = sentAt;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
