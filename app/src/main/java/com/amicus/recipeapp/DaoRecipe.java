package com.amicus.recipeapp;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface DaoRecipe {
    @Insert
    void insert(Meal meal);
    @Query("SELECT * FROM recipesDB")
    List<Meal> getAllRecipe();
    @Query("DELETE FROM recipesDB WHERE (id=:id)")
    void deleteByID(int id);
    @Query("UPDATE recipesDB  SET imageUrl=:imageUrl ,mealName=:mealName, ingredient1=:ingredient1,instructions=:instructions WHERE (id=:id)")
    void updateByID(int id,String imageUrl,String mealName, String ingredient1, String instructions);
    @Query("DELETE FROM recipesDB")
    void clearTable();
    @Query("SELECT id,mealName,imageUrl,ingredient1,instructions FROM recipesDB WHERE id=:id")
    Meal getRecipeById(int id);

}
