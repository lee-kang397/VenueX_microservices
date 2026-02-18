package com.example.auth_service.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role_name", unique = true, nullable = false)
    private String roleName; //GUEST, USER, HOST, ADMIN, SUPER_ADMIN

    // Add getters & setters
    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getType() {
        return roleName;
    }

    public void setType(String roleName) {
        this.roleName = roleName;
    }
}