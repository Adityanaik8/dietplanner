package com.nutriguide.dietplanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Component
public class FoodDataLoader implements CommandLineRunner {

    @Autowired
    private FoodRepository foodRepository;

    @Override
    public void run(String... args) throws Exception {
        if (foodRepository.count() == 0) {
            loadIndianFoods();
        }
    }

    private void loadIndianFoods() {
        List<FoodItem> indianFoods = Arrays.asList(
                createFood("Dal Tadka", "VEG", "Protein", 150.0, 8.0, 25.0, 4.0, 5.0, "lunch"),
                createFood("Paneer Butter Masala", "VEG", "Protein", 350.0, 18.0, 20.0, 25.0, 2.0, "lunch"),
                createFood("Chana Masala", "VEG", "Protein", 200.0, 10.0, 30.0, 8.0, 12.0, "lunch"),
                createFood("Rajma", "VEG", "Protein", 180.0, 9.0, 28.0, 6.0, 10.0, "lunch"),
                createFood("Vegetable Pulao", "VEG", "Carb", 250.0, 6.0, 45.0, 8.0, 4.0, "lunch"),
                createFood("Roti", "VEG", "Carb", 70.0, 3.0, 15.0, 2.0, 2.0, "lunch"),
                createFood("Rice", "VEG", "Carb", 130.0, 3.0, 28.0, 0.5, 0.5, "lunch"),
                createFood("Mixed Vegetable Sabzi", "VEG", "Vegetable", 80.0, 3.0, 10.0, 4.0, 6.0, "lunch"),
                createFood("Curd", "VEG", "Dairy", 60.0, 4.0, 4.0, 3.0, 0.0, "lunch"),

                createFood("Idli", "VEG", "Breakfast", 40.0, 2.0, 8.0, 0.5, 1.0, "breakfast"),
                createFood("Dosa", "VEG", "Breakfast", 120.0, 4.0, 20.0, 3.0, 2.0, "breakfast"),
                createFood("Poha", "VEG", "Breakfast", 150.0, 5.0, 30.0, 4.0, 3.0, "breakfast"),
                createFood("Upma", "VEG", "Breakfast", 180.0, 6.0, 25.0, 7.0, 3.0, "breakfast"),
                createFood("Paratha", "VEG", "Breakfast", 200.0, 6.0, 30.0, 8.0, 3.0, "breakfast"),

                createFood("Chicken Curry", "NON_VEG", "Protein", 220.0, 25.0, 8.0, 10.0, 2.0, "lunch"),
                createFood("Egg Curry", "NON_VEG", "Protein", 180.0, 15.0, 6.0, 12.0, 1.0, "lunch"),
                createFood("Fish Curry", "NON_VEG", "Protein", 200.0, 22.0, 5.0, 11.0, 1.0, "lunch"),
                createFood("Mutton Curry", "NON_VEG", "Protein", 280.0, 30.0, 8.0, 15.0, 2.0, "lunch"),
                createFood("Chicken Biryani", "NON_VEG", "Meal", 350.0, 20.0, 45.0, 12.0, 3.0, "lunch"),

                createFood("Boiled Egg", "NON_VEG", "Protein", 70.0, 6.0, 0.5, 5.0, 0.0, "breakfast"),
                createFood("Scrambled Eggs", "NON_VEG", "Protein", 150.0, 12.0, 2.0, 10.0, 0.0, "breakfast"),
                createFood("Omelette", "NON_VEG", "Protein", 180.0, 14.0, 3.0, 13.0, 1.0, "breakfast"),

                createFood("Fruit Salad", "VEG", "Snack", 100.0, 1.0, 25.0, 0.5, 4.0, "snack"),
                createFood("Roasted Chana", "VEG", "Snack", 120.0, 7.0, 20.0, 3.0, 8.0, "snack"),
                createFood("Nuts Mix", "VEG", "Snack", 160.0, 6.0, 8.0, 12.0, 3.0, "snack"),
                createFood("Buttermilk", "VEG", "Snack", 40.0, 2.0, 5.0, 1.0, 0.0, "snack")
        );

        foodRepository.saveAll(indianFoods);
    }

    private FoodItem createFood(String name, String foodType, String category,
                                Double calories, Double protein, Double carbs,
                                Double fat, Double fiber, String mealTime) {
        FoodItem food = new FoodItem();
        food.setName(name);
        food.setFoodType(foodType);
        food.setCategory(category);
        food.setCalories(calories);
        food.setProtein(protein);
        food.setCarbs(carbs);
        food.setFat(fat);
        food.setFiber(fiber);
        food.setMealTime(mealTime);
        food.setCuisine("Indian");
        return food;
    }
}