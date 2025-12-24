package com.nutriguide.dietplanner;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class CalorieCalculator {

    public Map<String, Object> calculateDailyCalories(User user) {
        Map<String, Object> result = new HashMap<>();

        double bmr;
        if ("MALE".equalsIgnoreCase(user.getDietType())) {
            bmr = 10 * user.getCurrentWeight() + 6.25 * user.getHeight() - 5 * user.getAge() + 5;
        } else {
            bmr = 10 * user.getCurrentWeight() + 6.25 * user.getHeight() - 5 * user.getAge() - 161;
        }

        double activityMultiplier = getActivityMultiplier(user.getActivityLevel());
        double maintenanceCalories = bmr * activityMultiplier;

        double targetCalories;
        String advice;

        if ("LOSE_WEIGHT".equalsIgnoreCase(user.getGoal())) {
            targetCalories = maintenanceCalories - 500;
            advice = "For weight loss, maintain a 500 calorie deficit";
        } else if ("GAIN_WEIGHT".equalsIgnoreCase(user.getGoal())) {
            targetCalories = maintenanceCalories + 500;
            advice = "For weight gain, maintain a 500 calorie surplus";
        } else {
            targetCalories = maintenanceCalories;
            advice = "For weight maintenance, eat at maintenance calories";
        }

        Map<String, Double> macros = calculateMacros(targetCalories, user.getGoal());

        result.put("bmr", (double) Math.round(bmr));
        result.put("maintenanceCalories", (double) Math.round(maintenanceCalories));
        result.put("targetCalories", (double) Math.round(targetCalories));
        result.put("advice", advice);
        result.put("macros", macros);

        return result;
    }

    private double getActivityMultiplier(String activityLevel) {
        if (activityLevel == null) return 1.2;

        switch (activityLevel.toUpperCase()) {
            case "SEDENTARY": return 1.2;
            case "LIGHT": return 1.375;
            case "ACTIVE": return 1.55;
            case "VERY_ACTIVE": return 1.725;
            default: return 1.2;
        }
    }

    private Map<String, Double> calculateMacros(double calories, String goal) {
        Map<String, Double> macros = new HashMap<>();

        double proteinCalories = calories * 0.3;
        double proteinGrams = proteinCalories / 4;

        double fatCalories = calories * 0.25;
        double fatGrams = fatCalories / 9;

        double carbCalories = calories * 0.45;
        double carbGrams = carbCalories / 4;

        macros.put("proteinGrams", (double) Math.round(proteinGrams));
        macros.put("fatGrams", (double) Math.round(fatGrams));
        macros.put("carbGrams", (double) Math.round(carbGrams));
        macros.put("proteinCalories", (double) Math.round(proteinCalories));
        macros.put("fatCalories", (double) Math.round(fatCalories));
        macros.put("carbCalories", (double) Math.round(carbCalories));

        return macros;
    }
}