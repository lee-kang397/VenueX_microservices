package com.venuex.transaction_service.DTO;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Integer id;
    private Integer userId;
    private Integer bookingId;
    private String type;
    private String status;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    public NotificationDTO() {
    }

    public NotificationDTO(Integer id, Integer userId, Integer bookingId, String type, String status,
            String message, LocalDateTime createdAt, LocalDateTime sentAt) {
        this.id = id;
        this.userId = userId;
        this.bookingId = bookingId;
        this.type = type;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}