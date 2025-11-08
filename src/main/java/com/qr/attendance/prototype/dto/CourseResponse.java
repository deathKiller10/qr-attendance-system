package com.qr.attendance.prototype.dto;

// This record will hold the simplified course data we send to the frontend.
public record CourseResponse(
        String id,
        String name,
        String description
) {
}