package com.nutriguide.dietplanner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByFoodType(String foodType);
    List<FoodItem> findByCategory(String category);
    List<FoodItem> findByMealTime(String mealTime);

    @Query("SELECT f FROM FoodItem f WHERE f.foodType = :foodType AND f.mealTime = :mealTime")
    List<FoodItem> findByFoodTypeAndMealTime(String foodType, String mealTime);

    List<FoodItem> findByNameContainingIgnoreCase(String name);
}