package com.qr.attendance.prototype.dto;

// This holds the token we send to the faculty to generate a QR code.
public record QrResponse(String token) {
}