package com.qr.attendance.prototype.controller;

import com.qr.attendance.prototype.dto.AttendanceReportItem; // NEW IMPORT
import com.qr.attendance.prototype.dto.AttendanceRequest;
import com.qr.attendance.prototype.dto.AttendanceResponse;
import com.qr.attendance.prototype.dto.QrResponse;
import com.qr.attendance.prototype.service.AttendanceService;
import com.qr.attendance.prototype.service.JwtService;

import org.springframework.format.annotation.DateTimeFormat; // NEW IMPORT
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate; // NEW IMPORT
import java.util.List; // NEW IMPORT

@RestController
@RequestMapping("/api/attendance")
@EnableMethodSecurity
public class AttendanceController {

    private final JwtService jwtService;
    private final AttendanceService attendanceService;

    public AttendanceController(JwtService jwtService, AttendanceService attendanceService) {
        this.jwtService = jwtService;
        this.attendanceService = attendanceService;
    }

    /**
     * Endpoint for FACULTY to generate a QR code token.
     * (This method already exists)
     */
    @GetMapping("/generate-qr")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<QrResponse> generateQrCode(@RequestParam String courseId) {
        String token = jwtService.generateToken(courseId);
        return ResponseEntity.ok(new QrResponse(token));
    }

    /**
     * Endpoint for STUDENTS to submit their scanned QR code token.
     * (This method already exists)
     */
    @PostMapping("/mark")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<AttendanceResponse> markAttendance(@RequestBody AttendanceRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        AttendanceResponse response = attendanceService.markAttendance(
                request.token(),
                authentication
        );

        if (!response.success()) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }


    // ===== NEW METHOD =====
    /**
     * Endpoint for FACULTY to get an attendance report for a specific course and date.
     */
    @GetMapping("/report/{courseId}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<List<AttendanceReportItem>> getAttendanceReport(
            @PathVariable String courseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        // Get the currently logged-in faculty member
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Call the service to get the report data
        List<AttendanceReportItem> report = attendanceService.getAttendanceReport(
                courseId,
                date,
                authentication
        );

        // Return the report as a JSON list
        return ResponseEntity.ok(report);
    }
}