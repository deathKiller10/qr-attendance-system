package com.qr.attendance.prototype.config;

import com.qr.attendance.prototype.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint; 
import org.springframework.security.core.userdetails.User;

import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Bean 1: The Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean 2: The User Details Service (FIXED)
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            com.qr.attendance.prototype.model.User appUser = userRepository.findById(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            // Determine if the role already has ROLE_ prefix
            String role = appUser.getRole();
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }

            return User.builder()
                    .username(appUser.getId())
                    .password(appUser.getPassword())
                    .roles(role.replace("ROLE_", "")) // roles() automatically adds ROLE_ prefix
                    .build();
        };
    }

    // Alternative UserDetailsService implementation using authorities():
    /*
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            com.qr.attendance.prototype.model.User appUser = userRepository.findById(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            return User.builder()
                    .username(appUser.getId())
                    .password(appUser.getPassword())
                    .authorities(appUser.getRole()) // Use exact role from database
                    .build();
        };
    }
    */

    // Bean 3: The Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    // Bean 4: The Security Context Repository
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    // Bean 5: The Security Filter Chain (FIXED)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) 
                
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                            "/", 
                            "/index.html", 
                            "/api/auth/login",
                            "/api/auth/me",
                            "/favicon.ico",
                            "/static/**", 
                            "/css/**", 
                            "/js/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                .securityContext(context -> context
                    .securityContextRepository(securityContextRepository)
                )

                // FIXED: Simplified logout configuration
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout") // Simple and clean
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )

                .exceptionHandling(ex -> 
                    ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                );

        return http.build();
    }
}