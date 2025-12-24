package com.nutriguide.dietplanner;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MealDetector {

    public Map<String, Object> detectMealScope(List<FoodItem> foods, Double userTargetCalories) {
        Map<String, Object> result = new HashMap<>();

        double totalCalories = 0;
        int foodCount = foods.size();

        for (FoodItem food : foods) {
            totalCalories += food.getCalories();
        }

        String detectedScope = detectScope(totalCalories, foodCount, userTargetCalories);
        String confidence = getConfidenceLevel(totalCalories, foodCount, userTargetCalories);
        List<String> possibleScopes = getPossibleScopes(totalCalories);

        result.put("totalCalories", Math.round(totalCalories));
        result.put("foodCount", foodCount);
        result.put("detectedScope", detectedScope);
        result.put("confidence", confidence);
        result.put("possibleScopes", possibleScopes);
        result.put("userMessage", generateUserMessage(detectedScope, totalCalories));

        return result;
    }

    private String detectScope(double calories, int foodCount, Double userTargetCalories) {
        if (userTargetCalories != null) {
            double percentage = (calories / userTargetCalories) * 100;

            if (percentage <= 25) return "ONE_MEAL";
            if (percentage <= 40) return "TWO_MEALS";
            if (percentage <= 70) return "THREE_MEALS";
            if (percentage <= 90) return "FULL_DAY_LIGHT";
            return "FULL_DAY";
        }

        if (calories < 300 && foodCount <= 3) return "ONE_MEAL";
        if (calories < 500 && foodCount <= 4) return "TWO_MEALS";
        if (calories < 800 && foodCount <= 6) return "THREE_MEALS";
        if (calories < 1200) return "FULL_DAY_LIGHT";
        return "FULL_DAY";
    }

    private String getConfidenceLevel(double calories, int foodCount, Double userTargetCalories) {
        if (userTargetCalories != null) {
            double diff = Math.abs(calories - userTargetCalories);
            double percentage = (diff / userTargetCalories) * 100;

            if (percentage < 10) return "HIGH";
            if (percentage < 25) return "MEDIUM";
            return "LOW";
        }

        if (calories < 200 || calories > 1500) return "HIGH";
        if (calories < 400 || calories > 1000) return "MEDIUM";
        return "LOW";
    }

    private List<String> getPossibleScopes(double calories) {
        List<String> scopes = new ArrayList<>();

        if (calories <= 400) {
            scopes.add("ONE_MEAL (Breakfast/Lunch/Dinner)");
        }

        if (calories >= 300 && calories <= 700) {
            scopes.add("TWO_MEALS (e.g., Lunch + Dinner)");
        }

        if (calories >= 500 && calories <= 900) {
            scopes.add("THREE_MEALS (Light meals)");
        }

        if (calories >= 800 && calories <= 1200) {
            scopes.add("FULL_DAY (Light activity)");
        }

        if (calories >= 1000) {
            scopes.add("FULL_DAY (Normal activity)");
        }

        if (calories >= 1500) {
            scopes.add("FULL_DAY (Active lifestyle)");
        }

        return scopes;
    }

    private String generateUserMessage(String scope, double calories) {
        switch (scope) {
            case "ONE_MEAL":
                return String.format("This looks like ONE MEAL (%.0f calories). Is this for breakfast, lunch, or dinner?", calories);
            case "TWO_MEALS":
                return String.format("This looks like TWO MEALS (%.0f calories). Is this for lunch+dinner or breakfast+lunch?", calories);
            case "THREE_MEALS":
                return String.format("This looks like THREE MEALS (%.0f calories). Is this for a full day of light eating?", calories);
            case "FULL_DAY_LIGHT":
                return String.format("This looks like a LIGHT FULL DAY plan (%.0f calories). Confirm if this is for entire day?", calories);
            case "FULL_DAY":
                return String.format("This looks like a FULL DAY meal plan (%.0f calories). Confirm if this covers all meals?", calories);
            default:
                return "Please specify if this is for one meal or full day.";
        }
    }

    public Map<String, Object> clarifyMealScope(String userResponse, List<FoodItem> foods) {
        Map<String, Object> result = new HashMap<>();

        String finalScope = determineFinalScope(userResponse);
        Map<String, Double> adjustedPortions = adjustPortions(finalScope, foods);

        result.put("finalScope", finalScope);
        result.put("adjustedPortions", adjustedPortions);
        result.put("recommendations", getScopeRecommendations(finalScope));

        return result;
    }

    private String determineFinalScope(String userResponse) {
        userResponse = userResponse.toLowerCase();

        if (userResponse.contains("breakfast") || userResponse.contains("lunch") ||
                userResponse.contains("dinner") || userResponse.contains("one meal") ||
                userResponse.contains("single")) {
            return "ONE_MEAL";
        }

        if (userResponse.contains("two") || userResponse.contains("both") ||
                userResponse.contains("lunch and dinner") || userResponse.contains("breakfast and lunch")) {
            return "TWO_MEALS";
        }

        if (userResponse.contains("full day") || userResponse.contains("whole day") ||
                userResponse.contains("all meals") || userResponse.contains("complete day")) {
            return "FULL_DAY";
        }

        return "UNKNOWN";
    }

    private Map<String, Double> adjustPortions(String scope, List<FoodItem> foods) {
        Map<String, Double> adjustments = new HashMap<>();

        switch (scope) {
            case "ONE_MEAL":
                adjustments.put("portion_multiplier", 1.0);
                adjustments.put("recommended_calories", 500.0);
                break;
            case "TWO_MEALS":
                adjustments.put("portion_multiplier", 0.7);
                adjustments.put("recommended_calories", 350.0);
                break;
            case "FULL_DAY":
                adjustments.put("portion_multiplier", 0.3);
                adjustments.put("recommended_calories", 200.0);
                break;
            default:
                adjustments.put("portion_multiplier", 1.0);
                adjustments.put("recommended_calories", 400.0);
        }

        return adjustments;
    }

    private List<String> getScopeRecommendations(String scope) {
        List<String> recommendations = new ArrayList<>();

        switch (scope) {
            case "ONE_MEAL":
                recommendations.add("Ensure this meal has protein, carbs, and vegetables");
                recommendations.add("Drink water before and after meal");
                recommendations.add("Eat slowly for better digestion");
                break;
            case "TWO_MEALS":
                recommendations.add("Space meals 4-5 hours apart");
                recommendations.add("Include protein in both meals");
                recommendations.add("Add a light snack if hungry between meals");
                break;
            case "FULL_DAY":
                recommendations.add("Eat every 3-4 hours");
                recommendations.add("Include breakfast within 1 hour of waking");
                recommendations.add("Finish dinner 2-3 hours before sleep");
                break;
        }

        return recommendations;
    }
}