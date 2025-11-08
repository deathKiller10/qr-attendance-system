package com.qr.attendance.prototype.repository;

import com.qr.attendance.prototype.model.AttendanceRecord;
import com.qr.attendance.prototype.model.Course;
import com.qr.attendance.prototype.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List; // NEW IMPORT
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, UUID> {

    /**
     * Finds an attendance record for a specific student and course
     * that occurred within a given time range (e.g., "today").
     * (This one already exists)
     */
    Optional<AttendanceRecord> findByStudentAndCourseAndTimestampBetween(
            User student,
            Course course,
            LocalDateTime start,
            LocalDateTime end
    );

    // ===== NEW METHOD =====
    /**
     * Finds ALL attendance records for a specific course within a
     * given time range, ordered by the timestamp.
     * This is what the faculty will use for the report.
     */
    List<AttendanceRecord> findAllByCourseAndTimestampBetweenOrderByTimestampAsc(
            Course course,
            LocalDateTime start,
            LocalDateTime end
    );
}