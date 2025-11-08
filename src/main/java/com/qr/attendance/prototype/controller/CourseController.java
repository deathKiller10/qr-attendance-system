package com.qr.attendance.prototype.controller;

import com.qr.attendance.prototype.dto.CourseResponse;
import com.qr.attendance.prototype.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getMyCourses() {
        // 1. Get the currently logged-in user from Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        // 2. Ask the CourseService to get the courses for this user
        List<CourseResponse> courses = courseService.getCoursesForUser(authentication);
        
        // 3. Return the list of courses as JSON
        return ResponseEntity.ok(courses);
    }
}