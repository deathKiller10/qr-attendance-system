package com.qr.attendance.prototype.repository;

import com.qr.attendance.prototype.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // Spring Data JPA will automatically create a query for us based on this method name:
    // "Find a User by their ID"
    Optional<User> findById(String id);
}