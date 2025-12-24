package com.nutriguide.dietplanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    @Autowired
    private MealPlanner mealPlanner;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodRepository foodRepository;

    @PostMapping("/generate/{userId}")
    public ResponseEntity<Map<String, Object>> generateMealPlan(
            @PathVariable Long userId,
            @RequestParam String dietType) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (!dietType.equals("VEG") && !dietType.equals("NON_VEG")) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, Object> mealPlan = mealPlanner.generateDailyMealPlan(user, dietType);
        return ResponseEntity.ok(mealPlan);
    }

    @GetMapping("/foods")
    public ResponseEntity<?> getAllFoods() {
        return ResponseEntity.ok(foodRepository.findAll());
    }

    @GetMapping("/foods/{type}")
    public ResponseEntity<?> getFoodsByType(@PathVariable String type) {
        return ResponseEntity.ok(foodRepository.findByFoodType(type));
    }
}