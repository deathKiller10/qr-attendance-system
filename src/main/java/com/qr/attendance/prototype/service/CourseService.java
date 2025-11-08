package com.qr.attendance.prototype.service;

import com.qr.attendance.prototype.dto.CourseResponse;
import com.qr.attendance.prototype.model.Course;
import com.qr.attendance.prototype.repository.CourseRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // We add @Transactional here. This is crucial!
    // It ensures that we can access lazy-loaded data (like course.getFaculty())
    // without getting a LazyInitializationException.
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesForUser(Authentication authentication) {
        String currentUserId = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User has no role"))
                .getAuthority();

        List<Course> courses;

        if ("ROLE_STUDENT".equals(role)) {
            courses = courseRepository.findByStudentsId(currentUserId);
            // Convert each Course object into a CourseResponse DTO
            return courses.stream()
                    .map(this::mapCourseToStudentResponse)
                    .collect(Collectors.toList());

        } else if ("ROLE_FACULTY".equals(role)) {
            courses = courseRepository.findByFacultyId(currentUserId);
            // Convert each Course object into a CourseResponse DTO
            return courses.stream()
                    .map(this::mapCourseToFacultyResponse)
                    .collect(Collectors.toList());
        }

        // If user has no valid role for this, return empty list
        return List.of();
    }

    // Helper method to convert a Course to a DTO for a student view
    private CourseResponse mapCourseToStudentResponse(Course course) {
        // This line works ONLY because the parent method is @Transactional
        String description = "Taught by: " + course.getFaculty().getName();
        return new CourseResponse(course.getId(), course.getName(), description);
    }

    // Helper method to convert a Course to a DTO for a faculty view
    private CourseResponse mapCourseToFacultyResponse(Course course) {
        // This line also works because of @Transactional
        String description = "Students Enrolled: " + course.getStudents().size();
        return new CourseResponse(course.getId(), course.getName(), description);
    }
}