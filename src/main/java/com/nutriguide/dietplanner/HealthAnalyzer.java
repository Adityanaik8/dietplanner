package com.nutriguide.dietplanner;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class HealthAnalyzer {

    public Map<String, Object> analyzeMeal(List<FoodItem> foods, String mealType) {
        Map<String, Object> result = new HashMap<>();

        double totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;
        double totalSugar = 0;
        double totalFiber = 0;

        int processedFoodCount = 0;
        int friedFoodCount = 0;
        int healthyFoodCount = 0;

        List<String> foodNames = new ArrayList<>();

        for (FoodItem food : foods) {
            foodNames.add(food.getName());
            totalCalories += food.getCalories();
            totalProtein += food.getProtein();
            totalCarbs += food.getCarbs();
            totalFat += food.getFat();
            totalFiber += food.getFiber();

            if (isProcessedFood(food.getName())) processedFoodCount++;
            if (isFriedFood(food.getName())) friedFoodCount++;
            if (isHealthyFood(food.getName())) healthyFoodCount++;
        }

        int healthScore = calculateHealthScore(totalCalories, totalProtein, totalCarbs,
                totalFat, totalFiber, processedFoodCount,
                friedFoodCount, healthyFoodCount, mealType);

        List<String> healthTips = generateHealthTips(healthScore, totalProtein, totalFiber,
                processedFoodCount, friedFoodCount);

        String healthStatus = getHealthStatus(healthScore);

        result.put("foodNames", foodNames);
        result.put("totalCalories", Math.round(totalCalories));
        result.put("totalProtein", Math.round(totalProtein));
        result.put("totalCarbs", Math.round(totalCarbs));
        result.put("totalFat", Math.round(totalFat));
        result.put("totalFiber", Math.round(totalFiber));
        result.put("processedFoodCount", processedFoodCount);
        result.put("friedFoodCount", friedFoodCount);
        result.put("healthyFoodCount", healthyFoodCount);
        result.put("healthScore", healthScore);
        result.put("healthStatus", healthStatus);
        result.put("healthTips", healthTips);
        result.put("mealType", mealType);

        return result;
    }

    private int calculateHealthScore(double calories, double protein, double carbs,
                                     double fat, double fiber, int processedCount,
                                     int friedCount, int healthyCount, String mealType) {
        int score = 100;

        double calorieLimit = getCalorieLimitForMeal(mealType);
        if (calories > calorieLimit) {
            score -= Math.min(30, (calories - calorieLimit) / 50);
        }

        if (protein < 15) score -= 20;
        else if (protein >= 25) score += 10;

        if (fiber < 5) score -= 15;
        else if (fiber >= 10) score += 10;

        if (fat > 30) score -= Math.min(20, (fat - 30) / 5);

        score -= (processedCount * 10);
        score -= (friedCount * 15);
        score += (healthyCount * 5);

        return Math.max(0, Math.min(100, score));
    }

    private double getCalorieLimitForMeal(String mealType) {
        switch (mealType.toLowerCase()) {
            case "breakfast": return 400;
            case "lunch": return 600;
            case "dinner": return 500;
            case "snack": return 200;
            default: return 500;
        }
    }

    private boolean isProcessedFood(String foodName) {
        String[] processedKeywords = {"burger", "pizza", "noodles", "maggi", "chips",
                "biscuit", "soda", "cold drink", "packaged"};
        foodName = foodName.toLowerCase();
        for (String keyword : processedKeywords) {
            if (foodName.contains(keyword)) return true;
        }
        return false;
    }

    private boolean isFriedFood(String foodName) {
        String[] friedKeywords = {"fried", "deep fry", "pakora", "bhaji", "samosa",
                "kachori", "puri", "vada"};
        foodName = foodName.toLowerCase();
        for (String keyword : friedKeywords) {
            if (foodName.contains(keyword)) return true;
        }
        return false;
    }

    private boolean isHealthyFood(String foodName) {
        String[] healthyKeywords = {"dal", "sabzi", "vegetable", "salad", "fruit",
                "curd", "sprouts", "boiled", "grilled", "steamed"};
        foodName = foodName.toLowerCase();
        for (String keyword : healthyKeywords) {
            if (foodName.contains(keyword)) return true;
        }
        return false;
    }

    private List<String> generateHealthTips(int score, double protein, double fiber,
                                            int processedCount, int friedCount) {
        List<String> tips = new ArrayList<>();

        if (score >= 80) {
            tips.add("Excellent meal! Keep it up!");
        } else if (score >= 60) {
            tips.add("Good meal! Some improvements possible.");
        } else {
            tips.add("Needs improvement for better health.");
        }

        if (protein < 15) {
            tips.add("Add more protein: dal, paneer, eggs, or chicken");
        }

        if (fiber < 5) {
            tips.add("Add vegetables or salad for more fiber");
        }

        if (processedCount > 0) {
            tips.add("Reduce processed foods for better health");
        }

        if (friedCount > 0) {
            tips.add("Try grilled or steamed instead of fried");
        }

        if (tips.size() == 1) {
            tips.add("Perfectly balanced meal!");
        }

        return tips;
    }

    private String getHealthStatus(int score) {
        if (score >= 90) return "EXCELLENT 🎉";
        if (score >= 80) return "VERY GOOD 👍";
        if (score >= 70) return "GOOD ✅";
        if (score >= 60) return "FAIR ⚠️";
        if (score >= 50) return "NEEDS IMPROVEMENT ❗";
        return "POOR 🚨";
    }
}