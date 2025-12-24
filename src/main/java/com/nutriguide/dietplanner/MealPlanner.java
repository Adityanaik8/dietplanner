package com.nutriguide.dietplanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MealPlanner {

    @Autowired
    private FoodRepository foodRepository;

    public Map<String, Object> generateDailyMealPlan(User user, String dietType) {
        Map<String, Object> result = new HashMap<>();

        Double targetCalories = getTargetCaloriesForGoal(user);
        Map<String, Double> macros = getMacroSplit(user.getGoal());

        Map<String, List<FoodItem>> dailyMeals = new LinkedHashMap<>();

        dailyMeals.put("breakfast", generateMeal("breakfast", dietType, targetCalories * 0.25));
        dailyMeals.put("lunch", generateMeal("lunch", dietType, targetCalories * 0.35));
        dailyMeals.put("snack", generateMeal("snack", dietType, targetCalories * 0.10));
        dailyMeals.put("dinner", generateMeal("dinner", dietType, targetCalories * 0.30));

        Map<String, Double> totalNutrition = calculateTotalNutrition(dailyMeals);

        result.put("dailyMeals", dailyMeals);
        result.put("totalNutrition", totalNutrition);
        result.put("targetCalories", targetCalories);
        result.put("macros", macros);
        result.put("dietType", dietType);
        result.put("advice", getDietAdvice(user.getGoal(), dietType));

        return result;
    }

    private List<FoodItem> generateMeal(String mealTime, String dietType, Double calorieTarget) {
        List<FoodItem> meal = new ArrayList<>();
        List<FoodItem> availableFoods;

        if ("NON_VEG".equalsIgnoreCase(dietType)) {
            availableFoods = foodRepository.findByFoodTypeAndMealTime("NON_VEG", mealTime);
        } else {
            availableFoods = foodRepository.findByFoodTypeAndMealTime("VEG", mealTime);
        }

        if (availableFoods.isEmpty()) {
            return meal;
        }

        Collections.shuffle(availableFoods);

        Double currentCalories = 0.0;
        for (FoodItem food : availableFoods) {
            if (currentCalories + food.getCalories() <= calorieTarget * 1.2) {
                meal.add(food);
                currentCalories += food.getCalories();
            }
            if (currentCalories >= calorieTarget * 0.8) {
                break;
            }
        }

        return meal;
    }

    private Double getTargetCaloriesForGoal(User user) {
        if ("LOSE_WEIGHT".equalsIgnoreCase(user.getGoal())) {
            return 1800.0;
        } else if ("GAIN_WEIGHT".equalsIgnoreCase(user.getGoal())) {
            return 2800.0;
        } else {
            return 2200.0;
        }
    }

    private Map<String, Double> getMacroSplit(String goal) {
        Map<String, Double> macros = new HashMap<>();

        if ("LOSE_WEIGHT".equalsIgnoreCase(goal)) {
            macros.put("protein", 40.0);
            macros.put("carbs", 30.0);
            macros.put("fat", 30.0);
        } else if ("GAIN_WEIGHT".equalsIgnoreCase(goal)) {
            macros.put("protein", 30.0);
            macros.put("carbs", 50.0);
            macros.put("fat", 20.0);
        } else {
            macros.put("protein", 35.0);
            macros.put("carbs", 40.0);
            macros.put("fat", 25.0);
        }

        return macros;
    }

    private Map<String, Double> calculateTotalNutrition(Map<String, List<FoodItem>> dailyMeals) {
        Map<String, Double> totals = new HashMap<>();
        totals.put("calories", 0.0);
        totals.put("protein", 0.0);
        totals.put("carbs", 0.0);
        totals.put("fat", 0.0);
        totals.put("fiber", 0.0);

        for (List<FoodItem> meal : dailyMeals.values()) {
            for (FoodItem food : meal) {
                totals.put("calories", totals.get("calories") + food.getCalories());
                totals.put("protein", totals.get("protein") + food.getProtein());
                totals.put("carbs", totals.get("carbs") + food.getCarbs());
                totals.put("fat", totals.get("fat") + food.getFat());
                totals.put("fiber", totals.get("fiber") + food.getFiber());
            }
        }

        return totals;
    }

    private String getDietAdvice(String goal, String dietType) {
        if ("LOSE_WEIGHT".equalsIgnoreCase(goal) && "VEG".equalsIgnoreCase(dietType)) {
            return "Focus on high-protein veg foods like dal, paneer, and legumes. Include plenty of vegetables.";
        } else if ("LOSE_WEIGHT".equalsIgnoreCase(goal) && "NON_VEG".equalsIgnoreCase(dietType)) {
            return "Choose lean proteins like chicken breast and fish. Avoid fried preparations.";
        } else if ("GAIN_WEIGHT".equalsIgnoreCase(goal) && "VEG".equalsIgnoreCase(dietType)) {
            return "Include calorie-dense foods like nuts, ghee, and whole grains with dal.";
        } else if ("GAIN_WEIGHT".equalsIgnoreCase(goal) && "NON_VEG".equalsIgnoreCase(dietType)) {
            return "Eat protein-rich meals with eggs, chicken, and fish. Include healthy carbs.";
        } else {
            return "Maintain balanced meals with proper portions.";
        }
    }
}
