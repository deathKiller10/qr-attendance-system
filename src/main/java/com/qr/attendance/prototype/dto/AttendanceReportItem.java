package com.qr.attendance.prototype.dto;

import java.time.LocalDateTime;

/**
 * A simple data object to hold the details of a single student's
 * attendance for the faculty report.
 */
public record AttendanceReportItem(
        String studentId,
        String studentName,
        LocalDateTime timestamp
) {
}