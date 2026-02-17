package com.venuex.host_mngt_service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.venuex.host_mngt_service.HostRequest.HostRequestStatus;
import com.venuex.host_mngt_service.user.User;

public interface HostRequestRepository extends JpaRepository<HostRequest, Integer> {
    boolean existsByUserAndStatus(User user, HostRequestStatus status);
}
