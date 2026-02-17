package com.venuex.host_mngt_service;

import java.time.LocalDateTime;

public class HostRequestDTO {

    private Integer id;
    private Integer userId;
    private String status;
    private LocalDateTime requestedTime;
    private Integer reviewedByUserId;

    public HostRequestDTO() {
    }

    public HostRequestDTO(Integer id, LocalDateTime requestedTime, Integer reviewedByUserId, String status, Integer userId) {
        this.id = id;
        this.requestedTime = requestedTime;
        this.reviewedByUserId = reviewedByUserId;
        this.status = status;
        this.userId = userId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(LocalDateTime requestedTime) {
        this.requestedTime = requestedTime;
    }

    public Integer getReviewedByUserId() {
        return reviewedByUserId;
    }

    public void setReviewedByUserId(Integer reviewedByUserId) {
        this.reviewedByUserId = reviewedByUserId;
    }
}
