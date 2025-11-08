package com.qr.attendance.prototype.dto;

// This class represents the JSON we will send BACK to the frontend.
// Notice we don't send the password back!
public record LoginResponse(String id, String name, String role) {
}