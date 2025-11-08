package com.qr.attendance.prototype.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // 1. This is our secret key. It's used to "sign" the tokens.
    // In a real production app, this would be in your application.properties
    // and be much more complex. For our project, this is perfect.
    // It MUST be a long, secure string.
    private final SecretKey secretKey = Keys.hmacShaKeyFor(
        "SecureSecretKeyForQRAttendanceProjectVITVelloreMCA".getBytes()
    );

    /**
     * Generates a new, short-lived JWT for a specific course.
     * This is what the faculty will generate.
     *
     * @param courseId The course (e.g., "CSE5001") this token is valid for.
     * @return A signed, 30-second-valid JWT string.
     */
    public String generateToken(String courseId) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // We set the token to expire in 30 seconds (30 * 1000 milliseconds)
        long expMillis = nowMillis + 60000; 
        Date exp = new Date(expMillis);

        return Jwts.builder()
                .subject(courseId) // The "subject" is the course ID
                .issuedAt(now)
                .expiration(exp)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Validates a token from a student's scan.
     *
     * @param token The JWT string from the QR code.
     * @return The course ID (e.g., "CSE5001") if the token is valid.
     * @throws io.jsonwebtoken.JwtException if the token is expired or invalid.
     */
    public String validateTokenAndGetCourseId(String token) {
        // This line does all the hard work:
        // 1. It checks the signature against our secretKey.
        // 2. It checks if the token is expired.
        // If either fails, it throws an exception.
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // If no exception was thrown, the token is valid.
        // We return the "subject", which is our course ID.
        return claims.getSubject();
    }
}