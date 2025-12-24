package com.nutriguide.dietplanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    @Autowired
    private UserIngredientRepository userIngredientRepository;

    @Autowired
    private SmartMealFinder smartMealFinder;

    @PostMapping("/add/{userId}")
    public ResponseEntity<Map<String, Object>> addIngredients(
            @PathVariable Long userId,
            @RequestBody List<Map<String, Object>> ingredients) {

        Map<String, Object> response = new HashMap<>();

        userIngredientRepository.deleteByUserId(userId);

        List<UserIngredient> savedIngredients = new ArrayList<>();

        for (Map<String, Object> ingredient : ingredients) {
            UserIngredient userIngredient = new UserIngredient();
            userIngredient.setUser(new User());
            userIngredient.getUser().setId(userId);

            userIngredient.setIngredientName((String) ingredient.get("name"));

            Object quantity = ingredient.get("quantity");
            if (quantity instanceof Integer) {
                userIngredient.setQuantity(((Integer) quantity).doubleValue());
            } else if (quantity instanceof Double) {
                userIngredient.setQuantity((Double) quantity);
            } else if (quantity instanceof String) {
                try {
                    userIngredient.setQuantity(Double.parseDouble((String) quantity));
                } catch (NumberFormatException e) {
                    userIngredient.setQuantity(1.0);
                }
            } else {
                userIngredient.setQuantity(1.0);
            }

            userIngredient.setUnit((String) ingredient.get("unit"));

            savedIngredients.add(userIngredientRepository.save(userIngredient));
        }

        response.put("success", true);
        response.put("message", "Ingredients saved successfully");
        response.put("count", savedIngredients.size());
        response.put("ingredients", savedIngredients);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserIngredient>> getUserIngredients(@PathVariable Long userId) {
        List<UserIngredient> ingredients = userIngredientRepository.findByUserId(userId);
        return ResponseEntity.ok(ingredients);
    }

    @PostMapping("/find-meals/{userId}")
    public ResponseEntity<Map<String, Object>> findMealsFromIngredients(
            @PathVariable Long userId,
            @RequestParam String dietType) {

        Map<String, Object> result = smartMealFinder.findMealsFromIngredients(userId, dietType);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Map<String, Object>> clearUserIngredients(@PathVariable Long userId) {
        userIngredientRepository.deleteByUserId(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Ingredients cleared successfully");

        return ResponseEntity.ok(response);
    }
}