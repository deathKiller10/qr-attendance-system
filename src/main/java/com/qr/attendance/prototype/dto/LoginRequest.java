package com.qr.attendance.prototype.dto;

// This is a "record", a modern Java feature for simple data-holding classes.
// It automatically creates a constructor, getters, equals(), and hashCode().
// This class represents the JSON the frontend will send TO us.
public record LoginRequest(String username, String password) {
}