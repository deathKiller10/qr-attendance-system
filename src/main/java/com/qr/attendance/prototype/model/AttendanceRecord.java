package com.qr.attendance.prototype.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendance_record")
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // We'll use a random, unique ID as the primary key

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private LocalDateTime timestamp; // The exact date and time of the scan

    // --- Constructors ---
    
    public AttendanceRecord() {
        // Default constructor for JPA
    }

    public AttendanceRecord(User student, Course course) {
        this.student = student;
        this.course = course;
        this.timestamp = LocalDateTime.now(); // Automatically set to "now"
    }

    // --- Getters and Setters ---
    // (You can generate these in your IDE)
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}