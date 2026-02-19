package com.venuex.host_mngt_service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.venuex.host_mngt_service.HostRequest.HostRequestStatus;

public interface HostRequestRepository extends JpaRepository<HostRequest, Integer> {
    boolean existsByUserIdAndStatus(Integer userId, HostRequestStatus status);
}
