package com.amicus.recipeapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeResponce {
    @SerializedName("meals")
    private List<Meal> meals;

    public List<Meal> getMeals() {
        return meals;
    }
}
