package com.nutriguide.dietplanner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserIngredientRepository extends JpaRepository<UserIngredient, Long> {
    List<UserIngredient> findByUserId(Long userId);
    void deleteByUserId(Long userId);
    List<UserIngredient> findByUserIdAndIngredientNameContainingIgnoreCase(Long userId, String ingredientName);
}