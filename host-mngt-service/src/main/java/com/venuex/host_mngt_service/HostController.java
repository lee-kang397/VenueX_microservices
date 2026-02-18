package com.venuex.host_mngt_service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class HostController {

    private final HostService hostService;

    public HostController(HostService hostService) {
        this.hostService = hostService;
    }

    //User request to be a host 
    @PostMapping("/user/hosts/request")
    @ResponseStatus(HttpStatus.CREATED)
    public String createHostRequest(HttpServletRequest request) {
        Integer hostId = (Integer) request.getAttribute("userId");
        String role = (String) request.getAttribute("userRole");
        return hostService.createHostRequest(hostId,role);
    }

    //ADMIN, get host requests 
    @GetMapping("/admin/hosts/request")
    @ResponseStatus(HttpStatus.OK)
    public List<HostRequestDTO> getALLHostRequests (HttpServletRequest request) {
        String role = (String) request.getAttribute("userRole");
        return hostService.getAllHostRequests(role);
    }

    @PutMapping("/admin/hosts/requests/{id}/approve")
    @ResponseStatus(HttpStatus.OK)
    public void approveHostRequest(@PathVariable Integer id, HttpServletRequest request) {

        Integer adminId = (Integer) request.getAttribute("userId");
        String role = (String) request.getAttribute("userRole");

        hostService.approveHostRequest(id, adminId, role);
    }

    @PutMapping("/admin/hosts/requests/{id}/deny")
    @ResponseStatus(HttpStatus.OK)
    public void denyHostRequest(@PathVariable Integer id, HttpServletRequest request) {

        Integer adminId = (Integer) request.getAttribute("userId");
        String role = (String) request.getAttribute("userRole");

        hostService.denyHostRequest(id, adminId, role); 
    }

    @DeleteMapping("/host/requests/{id}")
    @ResponseStatus(HttpStatus.OK) 
    public void deleteHostRequest(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        String role = (String) request.getAttribute("userRole");
        hostService.deleteHostRequest(id, userId, role);
    }
}
