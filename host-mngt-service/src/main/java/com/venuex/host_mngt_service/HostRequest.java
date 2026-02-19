package com.venuex.host_mngt_service;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
@Entity
@Table(name = "host_requests")
public class HostRequest {
    public enum HostRequestStatus {
        PENDING,
        APPROVED,
        DENIED
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HostRequestStatus status;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedTime;
    
    @PrePersist
    protected void onCreate() {
        this.requestedTime = LocalDateTime.now();
    }

    @Column(name = "reviewed_by")
    private Integer reviewedBy;

    public HostRequest() {
    }

    public HostRequest(Integer id, Integer userId,HostRequestStatus status, LocalDateTime requestedTime, Integer reviewedBy) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.requestedTime = requestedTime;
        this.reviewedBy = reviewedBy;
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

    public HostRequestStatus getStatus() {
        return status;
    }

    public void setStatus(HostRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(LocalDateTime requestedTime) {
        this.requestedTime = requestedTime;
    }

    public Integer getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Integer reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

}
