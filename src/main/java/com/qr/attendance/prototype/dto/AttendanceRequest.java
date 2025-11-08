package com.qr.attendance.prototype.dto;

// This holds the token the student sends us from their scan.
public record AttendanceRequest(String token) {
}