package com.qr.attendance.prototype.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "course")
public class Course {

    @Id
    @Column(nullable = false, unique = true)
    private String id; // e.g., "CSE5001"

    @Column(nullable = false)
    private String name; // e.g., "Java Programming"

    // --- Relationship 1: Faculty (Many-to-One) ---
    // Many courses can be taught by one faculty member.
    @ManyToOne(fetch = FetchType.LAZY) // LAZY = Don't load this user unless we ask for it
    @JoinColumn(name = "faculty_id") // This will create a 'faculty_id' column in the 'course' table
    private User faculty;

    // --- Relationship 2: Students (Many-to-Many) ---
    // Many students can be in many courses.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "course_enrollment", // Name of the "join table" that links courses and students
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<User> students = new HashSet<>(); // A Set ensures no duplicate students

    // --- Constructors ---
    public Course() {
        // Default constructor for JPA
    }

    public Course(String id, String name, User faculty) {
        this.id = id;
        this.name = name;
        this.faculty = faculty;
    }

    // --- Getters and Setters ---
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

    public User getFaculty() {
        return faculty;
    }

    public void setFaculty(User faculty) {
        this.faculty = faculty;
    }

    public Set<User> getStudents() {
        return students;
    }

    public void setStudents(Set<User> students) {
        this.students = students;
    }

    // --- Helper methods for convenience ---
    public void addStudent(User student) {
        this.students.add(student);
        // Note: In a full app, you'd also add the course to the student's list
        // to keep both sides of the relationship in sync.
    }

    // --- equals() and hashCode() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}