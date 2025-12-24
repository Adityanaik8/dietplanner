package com.nutriguide.dietplanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/calories")
public class CalorieController {

    @Autowired
    private CalorieCalculator calorieCalculator;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/calculate/{userId}")
    public ResponseEntity<Map<String, Object>> calculateCalories(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> result = calorieCalculator.calculateDailyCalories(user);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/quick-calculate")
    public ResponseEntity<Map<String, Object>> quickCalculate(
            @RequestParam double weight,
            @RequestParam double height,
            @RequestParam int age,
            @RequestParam String activityLevel,
            @RequestParam String goal) {

        User tempUser = new User();
        tempUser.setCurrentWeight(weight);
        tempUser.setHeight(height);
        tempUser.setAge(age);
        tempUser.setActivityLevel(activityLevel);
        tempUser.setGoal(goal);

        Map<String, Object> result = calorieCalculator.calculateDailyCalories(tempUser);
        return ResponseEntity.ok(result);
    }
}