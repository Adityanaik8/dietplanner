package com.nutriguide.dietplanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        if (userRepository.findByUsername(user.getUsername()) != null) {
            response.put("success", false);
            response.put("message", "Username already exists");
            return ResponseEntity.badRequest().body(response);
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            response.put("success", false);
            response.put("message", "Email already registered");
            return ResponseEntity.badRequest().body(response);
        }

        User savedUser = userRepository.save(user);

        response.put("success", true);
        response.put("message", "User registered successfully");
        response.put("userId", savedUser.getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> loginData) {
        Map<String, Object> response = new HashMap<>();

        String username = loginData.get("username");
        String password = loginData.get("password");

        User user = userRepository.findByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            response.put("success", false);
            response.put("message", "Invalid username or password");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("success", true);
        response.put("message", "Login successful");
        response.put("user", user);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserProfile(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }
}