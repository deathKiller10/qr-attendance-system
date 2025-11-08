package com.qr.attendance.prototype.dto;

// This is the success/error message we send back to the student.
public record AttendanceResponse(boolean success, String message) {
}