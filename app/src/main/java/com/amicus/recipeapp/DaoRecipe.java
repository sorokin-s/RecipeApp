package com.amicus.recipeapp;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
@Dao
public interface DaoRecipe {
    @Insert
    void insert(Meal meal);
    @Query("SELECT * FROM recipesDB")
    List<Meal> getAllRecipe();
    @Query("DELETE FROM recipesDB WHERE (mealName=:mealName)")
    void deleteByName(String mealName);
    @Query("DELETE FROM recipesDB")
    void clearTable();
}
