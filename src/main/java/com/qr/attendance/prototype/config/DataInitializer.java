package com.qr.attendance.prototype.config;

import com.qr.attendance.prototype.model.Course;
import com.qr.attendance.prototype.model.User;
import com.qr.attendance.prototype.repository.CourseRepository;
import com.qr.attendance.prototype.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CourseRepository courseRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        
        // Check if we already have users.
        if (userRepository.count() == 0) {
            System.out.println("No users found. Creating sample data...");

            // === 1. CREATE USERS ===
            
            // --- Student 1 ---
            User student1 = new User(
                    "22MCA1001",
                    "R. Sharma",
                    passwordEncoder.encode("123"),
                    "ROLE_STUDENT"
            );

            // --- Student 2 (NEW) ---
            User student2 = new User(
                    "22MCA1002",
                    "P. Singh",
                    passwordEncoder.encode("123"),
                    "ROLE_STUDENT"
            );

            // --- Faculty 1 ---
            User faculty1 = new User(
                    "F100",
                    "Prof. S. Rajan",
                    passwordEncoder.encode("456"),
                    "ROLE_FACULTY"
            );
            
            // --- Faculty 2 (NEW) ---
            User faculty2 = new User(
                    "F101",
                    "Dr. A. Gupta",
                    passwordEncoder.encode("456"),
                    "ROLE_FACULTY"
            );

            // Save all users to the database
            userRepository.saveAll(List.of(student1, student2, faculty1, faculty2));
            System.out.println("Sample students and faculty created.");

            // === 2. CREATE COURSES ===
            
            // Course 1, taught by Faculty 1
            Course javaCourse = new Course("CSE5001", "Java Programming", faculty1);
            
            // Course 2, also taught by Faculty 1
            Course dsCourse = new Course("CSE5002", "Data Structures", faculty1);

            // Course 3 (NEW), taught by Faculty 2
            Course aiCourse = new Course("CSE6001", "Advanced AI", faculty2);
            
            System.out.println("Sample courses created.");

            // === 3. ENROLL STUDENTS (THE TEST SCENARIO) ===
            
            // Java (CSE5001) has BOTH students
            javaCourse.addStudent(student1);
            javaCourse.addStudent(student2);
            
            // Data Structures (CSE5002) has ONLY student 1
            dsCourse.addStudent(student1);
            
            // Advanced AI (CSE6001) has ONLY student 2
            aiCourse.addStudent(student2);

            // Save the courses (which also saves the enrollments)
            courseRepository.saveAll(List.of(javaCourse, dsCourse, aiCourse));
            
            System.out.println("Students enrolled in courses.");

        } else {
            System.out.println("Database already contains data. Skipping initialization.");
        }
    }
}