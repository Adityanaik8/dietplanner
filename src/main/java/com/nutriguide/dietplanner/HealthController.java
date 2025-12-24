package com.nutriguide.dietplanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private HealthAnalyzer healthAnalyzer;

    @Autowired
    private MealDetector mealDetector;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/analyze-meal")
    public ResponseEntity<Map<String, Object>> analyzeMeal(
            @RequestBody List<Map<String, Object>> foodData,
            @RequestParam(defaultValue = "lunch") String mealType) {

        List<FoodItem> foods = convertToFoodItems(foodData);
        Map<String, Object> analysis = healthAnalyzer.analyzeMeal(foods, mealType);

        return ResponseEntity.ok(analysis);
    }

    @PostMapping("/detect-scope/{userId}")
    public ResponseEntity<Map<String, Object>> detectMealScope(
            @PathVariable Long userId,
            @RequestBody List<Map<String, Object>> foodData) {

        User user = userRepository.findById(userId).orElse(null);
        Double userTargetCalories = null;

        if (user != null) {
            Map<String, Object> calorieData = new CalorieCalculator().calculateDailyCalories(user);
            userTargetCalories = (Double) calorieData.get("targetCalories");
        }

        List<FoodItem> foods = convertToFoodItems(foodData);
        Map<String, Object> detection = mealDetector.detectMealScope(foods, userTargetCalories);

        return ResponseEntity.ok(detection);
    }

    @PostMapping("/clarify-scope")
    public ResponseEntity<Map<String, Object>> clarifyMealScope(
            @RequestParam String userResponse,
            @RequestBody List<Map<String, Object>> foodData) {

        List<FoodItem> foods = convertToFoodItems(foodData);
        Map<String, Object> clarification = mealDetector.clarifyMealScope(userResponse, foods);

        return ResponseEntity.ok(clarification);
    }

    @PostMapping("/complete-analysis/{userId}")
    public ResponseEntity<Map<String, Object>> completeHealthAnalysis(
            @PathVariable Long userId,
            @RequestBody List<Map<String, Object>> foodData,
            @RequestParam(defaultValue = "lunch") String mealType) {

        Map<String, Object> result = new HashMap<>();

        User user = userRepository.findById(userId).orElse(null);
        List<FoodItem> foods = convertToFoodItems(foodData);

        Map<String, Object> healthAnalysis = healthAnalyzer.analyzeMeal(foods, mealType);

        Double userTargetCalories = null;
        if (user != null) {
            Map<String, Object> calorieData = new CalorieCalculator().calculateDailyCalories(user);
            userTargetCalories = (Double) calorieData.get("targetCalories");
        }

        Map<String, Object> scopeDetection = mealDetector.detectMealScope(foods, userTargetCalories);

        result.put("healthAnalysis", healthAnalysis);
        result.put("scopeDetection", scopeDetection);
        result.put("recommendations", generateRecommendations(healthAnalysis, scopeDetection));
        result.put("timestamp", new Date());

        return ResponseEntity.ok(result);
    }

    private List<FoodItem> convertToFoodItems(List<Map<String, Object>> foodData) {
        List<FoodItem> foods = new ArrayList<>();

        for (Map<String, Object> foodMap : foodData) {
            FoodItem food = new FoodItem();
            food.setName((String) foodMap.get("name"));

            Object calories = foodMap.get("calories");
            if (calories instanceof Integer) {
                food.setCalories(((Integer) calories).doubleValue());
            } else if (calories instanceof Double) {
                food.setCalories((Double) calories);
            } else {
                food.setCalories(200.0);
            }

            Object protein = foodMap.get("protein");
            if (protein instanceof Integer) {
                food.setProtein(((Integer) protein).doubleValue());
            } else if (protein instanceof Double) {
                food.setProtein((Double) protein);
            } else {
                food.setProtein(10.0);
            }

            Object carbs = foodMap.get("carbs");
            if (carbs instanceof Integer) {
                food.setCarbs(((Integer) carbs).doubleValue());
            } else if (carbs instanceof Double) {
                food.setCarbs((Double) carbs);
            } else {
                food.setCarbs(20.0);
            }

            Object fat = foodMap.get("fat");
            if (fat instanceof Integer) {
                food.setFat(((Integer) fat).doubleValue());
            } else if (fat instanceof Double) {
                food.setFat((Double) fat);
            } else {
                food.setFat(8.0);
            }

            foods.add(food);
        }

        return foods;
    }

    private List<String> generateRecommendations(Map<String, Object> healthAnalysis,
                                                 Map<String, Object> scopeDetection) {
        List<String> recommendations = new ArrayList<>();

        int healthScore = (int) healthAnalysis.get("healthScore");
        String healthStatus = (String) healthAnalysis.get("healthStatus");
        String detectedScope = (String) scopeDetection.get("detectedScope");

        if (healthScore < 70) {
            recommendations.add("Improve meal health score by adding more vegetables");
        }

        if ("ONE_MEAL".equals(detectedScope)) {
            recommendations.add("For single meal, ensure it has balanced nutrition");
        } else if ("FULL_DAY".equals(detectedScope)) {
            recommendations.add("For full day, add snacks between meals");
        }

        if (healthScore >= 80) {
            recommendations.add("Great job! Maintain this eating pattern");
        }

        if (recommendations.isEmpty()) {
            recommendations.add("Your meal plan looks good. Stay consistent!");
        }

        return recommendations;
    }
}