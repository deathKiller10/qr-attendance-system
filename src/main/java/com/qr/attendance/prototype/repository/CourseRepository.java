package com.qr.attendance.prototype.repository;

import com.qr.attendance.prototype.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    // Spring Data JPA will automatically understand these method names
    // and build the correct SQL query for us.

    /**
     * Finds all courses taught by a specific faculty member.
     * This works because our Course entity has a 'faculty' field,
     * and that faculty has an 'id' field.
     */
    List<Course> findByFacultyId(String facultyId);

    /**
     * Finds all courses a specific student is enrolled in.
     * This works because our Course entity has a 'students' field (the Set),
     * and those students have an 'id' field.
     */
    List<Course> findByStudentsId(String studentId);
}