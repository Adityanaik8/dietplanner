package com.nutriguide.dietplanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SmartMealFinder {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private UserIngredientRepository userIngredientRepository;

    public Map<String, Object> findMealsFromIngredients(Long userId, String dietType) {
        Map<String, Object> result = new HashMap<>();

        List<UserIngredient> userIngredients = userIngredientRepository.findByUserId(userId);
        List<String> ingredientNames = new ArrayList<>();

        for (UserIngredient ui : userIngredients) {
            ingredientNames.add(ui.getIngredientName().toLowerCase());
        }

        List<FoodItem> allFoods;
        if ("NON_VEG".equalsIgnoreCase(dietType)) {
            allFoods = foodRepository.findByFoodType("NON_VEG");
        } else {
            allFoods = foodRepository.findByFoodType("VEG");
        }

        Map<String, List<FoodItem>> suggestedMeals = new HashMap<>();
        suggestedMeals.put("breakfast", new ArrayList<>());
        suggestedMeals.put("lunch", new ArrayList<>());
        suggestedMeals.put("dinner", new ArrayList<>());
        suggestedMeals.put("snack", new ArrayList<>());

        for (FoodItem food : allFoods) {
            String foodName = food.getName().toLowerCase();

            boolean hasCommonIngredient = false;
            for (String ingredient : ingredientNames) {
                if (foodName.contains(ingredient) || ingredient.contains(foodName)) {
                    hasCommonIngredient = true;
                    break;
                }
            }

            if (hasCommonIngredient) {
                String mealTime = food.getMealTime();
                if (mealTime != null && suggestedMeals.containsKey(mealTime)) {
                    suggestedMeals.get(mealTime).add(food);
                }
            }
        }

        Map<String, Double> nutritionScore = calculateNutritionScore(suggestedMeals);

        result.put("availableIngredients", ingredientNames);
        result.put("suggestedMeals", suggestedMeals);
        result.put("nutritionScore", nutritionScore);
        result.put("missingNutrients", findMissingNutrients(suggestedMeals));
        result.put("shoppingSuggestions", getShoppingSuggestions(suggestedMeals));

        return result;
    }

    private Map<String, Double> calculateNutritionScore(Map<String, List<FoodItem>> meals) {
        Map<String, Double> score = new HashMap<>();
        double totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;
        double totalFiber = 0;

        for (List<FoodItem> mealList : meals.values()) {
            for (FoodItem food : mealList) {
                totalCalories += food.getCalories();
                totalProtein += food.getProtein();
                totalCarbs += food.getCarbs();
                totalFat += food.getFat();
                totalFiber += food.getFiber();
            }
        }

        score.put("totalCalories", totalCalories);
        score.put("totalProtein", totalProtein);
        score.put("totalCarbs", totalCarbs);
        score.put("totalFat", totalFat);
        score.put("totalFiber", totalFiber);

        double completenessScore = 0;
        if (totalProtein >= 50) completenessScore += 25;
        if (totalCarbs >= 150) completenessScore += 25;
        if (totalFat >= 40) completenessScore += 25;
        if (totalFiber >= 25) completenessScore += 25;

        score.put("completenessScore", completenessScore);

        return score;
    }

    private List<String> findMissingNutrients(Map<String, List<FoodItem>> meals) {
        List<String> missing = new ArrayList<>();

        Map<String, Double> score = calculateNutritionScore(meals);

        if (score.get("totalProtein") < 50) missing.add("Protein - Add dal, paneer, or eggs");
        if (score.get("totalCarbs") < 150) missing.add("Carbs - Add rice, roti, or bread");
        if (score.get("totalFiber") < 25) missing.add("Fiber - Add vegetables or fruits");
        if (score.get("totalFat") < 40) missing.add("Healthy fats - Add nuts, ghee, or oil");

        return missing;
    }

    private List<String> getShoppingSuggestions(Map<String, List<FoodItem>> meals) {
        List<String> suggestions = new ArrayList<>();

        Map<String, Double> score = calculateNutritionScore(meals);

        if (score.get("totalProtein") < 50) {
            suggestions.add("Buy: Eggs, Paneer, Dal, or Chicken");
        }
        if (score.get("totalFiber") < 25) {
            suggestions.add("Buy: Spinach, Carrots, Apples, or Salad vegetables");
        }
        if (score.get("completenessScore") < 50) {
            suggestions.add("Buy: Complete meal ingredients - Rice, Dal, Vegetables");
        }

        if (suggestions.isEmpty()) {
            suggestions.add("You have enough ingredients for balanced meals!");
        }

        return suggestions;
    }
}