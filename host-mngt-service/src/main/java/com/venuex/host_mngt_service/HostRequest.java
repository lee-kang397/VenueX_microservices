package com.venuex.host_mngt_service;

import java.time.LocalDateTime;

import com.venuex.host_mngt_service.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HostRequestStatus status;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedTime;
    
    @PrePersist
    protected void onCreate() {
        this.requestedTime = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY, optional=true)
    @JoinColumn(name = "reviewed_by", nullable = true)
    private User reviewedBy;

    public HostRequest() {
    }

    public HostRequest(Integer id, User user,HostRequestStatus status, LocalDateTime requestedTime, User reviewedBy) {
        this.id = id;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

}
