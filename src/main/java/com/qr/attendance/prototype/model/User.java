package com.qr.attendance.prototype.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "app_user") // "user" is a reserved keyword in SQL, so we use "app_user"
public class User {

    @Id
    @Column(unique = true, nullable = false)
    private String id; // This will be the Registration Number (e.g., 22MCA1001) or Faculty ID

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // We'll store "ROLE_STUDENT" or "ROLE_FACULTY"

    // --- Constructors ---
    
    public User() {
        // Default constructor required by JPA
    }

    public User(String id, String name, String password, String role) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    // --- Getters and Setters ---
    // These are necessary for JPA and Spring
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // --- equals() and hashCode() ---
    // Good practice for entities

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}