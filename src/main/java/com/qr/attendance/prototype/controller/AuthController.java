package com.qr.attendance.prototype.controller;

import com.qr.attendance.prototype.dto.LoginRequest;
import com.qr.attendance.prototype.dto.LoginResponse;
import com.qr.attendance.prototype.model.User;
import com.qr.attendance.prototype.repository.UserRepository;

// Imports for session management
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.SecurityContextRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping; // This is the new import

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final SecurityContextRepository securityContextRepository;

    // Updated constructor to inject the repository
    public AuthController(AuthenticationManager authenticationManager, 
                          UserRepository userRepository,
                          SecurityContextRepository securityContextRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.securityContextRepository = securityContextRepository;
    }

    // This is the login method we fixed to properly save the session
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, 
                                              HttpServletRequest request, 
                                              HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            // This part explicitly saves the login to the session cookie
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            securityContextRepository.saveContext(context, request, response);

            User user = userRepository.findById(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found after authentication"));

            LoginResponse responseDto = new LoginResponse(user.getId(), user.getName(), user.getRole());
            return ResponseEntity.ok(responseDto);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed: " + e.getMessage());
        }
    }

    // This is the new method for Feature #2
    /**
     * "Who Am I?" endpoint.
     * Checks the session cookie to see if a user is already authenticated.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        // Spring Security's context will be populated if the user has a valid JSESSIONID cookie
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated and not an "anonymous" user
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            // No valid session, send 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session");
        }

        // We have a user. Fetch their details from our database.
        User user = userRepository.findById(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));

        // Return the same DTO as the login endpoint
        LoginResponse responseDto = new LoginResponse(user.getId(), user.getName(), user.getRole());
        return ResponseEntity.ok(responseDto);
    }
}