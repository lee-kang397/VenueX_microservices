package com.venuex.transaction_service.DTO;

import java.time.LocalDateTime;

import com.venuex.transaction_service.entities.Notification;

public class NotificationDTO {

    private Integer id;
    private Integer bookingId;
    private String message;
    private Notification.NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    public NotificationDTO() {
    }

    public NotificationDTO(Integer id,
            Integer bookingId,
            String message,
            Notification.NotificationStatus status,
            LocalDateTime createdAt,
            LocalDateTime sentAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Notification.NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(Notification.NotificationStatus status) {
        this.status = status;
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
