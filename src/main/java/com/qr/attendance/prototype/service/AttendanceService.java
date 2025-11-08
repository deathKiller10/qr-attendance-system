package com.qr.attendance.prototype.service;

import com.qr.attendance.prototype.dto.AttendanceReportItem; // NEW IMPORT
import com.qr.attendance.prototype.dto.AttendanceResponse;
import com.qr.attendance.prototype.model.AttendanceRecord;
import com.qr.attendance.prototype.model.Course;
import com.qr.attendance.prototype.model.User;
import com.qr.attendance.prototype.repository.AttendanceRecordRepository;
import com.qr.attendance.prototype.repository.CourseRepository;
import com.qr.attendance.prototype.repository.UserRepository;

import io.jsonwebtoken.JwtException;
import org.springframework.security.access.AccessDeniedException; // NEW IMPORT
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate; // NEW IMPORT
import java.time.LocalTime;
import java.util.List; // NEW IMPORT
import java.util.stream.Collectors; // NEW IMPORT

@Service
public class AttendanceService {

    private final JwtService jwtService;
    private final AttendanceRecordRepository attendanceRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public AttendanceService(JwtService jwtService,
                             AttendanceRecordRepository attendanceRepository,
                             UserRepository userRepository,
                             CourseRepository courseRepository) {
        this.jwtService = jwtService;
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Handles the student's attempt to mark attendance.
     * (This method already exists)
     */
    @Transactional
    public AttendanceResponse markAttendance(String token, Authentication authentication) {
        String studentId = authentication.getName();
        String courseId;

        // 1. Validate the token (is it real? is it expired?)
        try {
            courseId = jwtService.validateTokenAndGetCourseId(token);
        } catch (JwtException e) {
            // e.g., TokenExpiredException, SignatureException
            return new AttendanceResponse(false, "Invalid or expired QR code.");
        }

        // 2. Fetch the student and course from the database
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalStateException("Student not found."));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalStateException("Course not found."));

        // 3. Check if the student is actually enrolled in this course
        if (!course.getStudents().contains(student)) {
            return new AttendanceResponse(false, "You are not enrolled in this course.");
        }

        // 4. Check if the student has ALREADY marked attendance for this course today
        LocalDate today = LocalDate.now();
        boolean alreadyMarked = attendanceRepository.findByStudentAndCourseAndTimestampBetween(
                student,
                course,
                today.atStartOfDay(),
                today.atTime(LocalTime.MAX)
        ).isPresent();

        if (alreadyMarked) {
            return new AttendanceResponse(false, "Attendance already marked for this course today.");
        }

        // 5. ALL CHECKS PASSED. Mark attendance.
        AttendanceRecord newRecord = new AttendanceRecord(student, course);
        attendanceRepository.save(newRecord);

        return new AttendanceResponse(true, "Attendance marked successfully for " + course.getName() + "!");
    }


    // ===== NEW METHOD =====
    /**
     * Gets the attendance report for a specific course on a specific date.
     *
     * @param courseId The ID of the course (e.g., "CSE5001").
     * @param date The date for the report (e.g., "2025-11-08").
     * @param authentication The currently logged-in faculty member.
     * @return A list of student attendance records.
     */
    @Transactional(readOnly = true)
    public List<AttendanceReportItem> getAttendanceReport(String courseId, LocalDate date, Authentication authentication) {
        String facultyId = authentication.getName();
        
        // 1. Find the course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found."));

        // 2. SECURITY CHECK: Does this faculty teach this course?
        if (course.getFaculty() == null || !course.getFaculty().getId().equals(facultyId)) {
            // If not, throw an Access Denied error (which will become a 403 Forbidden)
            throw new AccessDeniedException("You are not authorized to view reports for this course.");
        }

        // 3. Find the records for this course on the given date
        List<AttendanceRecord> records = attendanceRepository.findAllByCourseAndTimestampBetweenOrderByTimestampAsc(
                course,
                date.atStartOfDay(),
                date.atTime(LocalTime.MAX)
        );

        // 4. Convert the database objects (AttendanceRecord) into our simple DTOs (AttendanceReportItem)
        return records.stream()
                .map(record -> new AttendanceReportItem(
                        record.getStudent().getId(),
                        record.getStudent().getName(),
                        record.getTimestamp()
                ))
                .collect(Collectors.toList());
    }
}