package com.amicus.recipeapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Meal {

    @SerializedName("strMeal")
    @Expose
    private String name;
    @SerializedName("strInstructions")
    @Expose
    private String instructions;
    @SerializedName("strMealThumb")
    @Expose
    private String imageUrl;
    @SerializedName("strIngredient1")
    @Expose
    private String ingredient1;
    @SerializedName("strIngredient2")
    @Expose
    private String ingredient2;
    @SerializedName("strIngredient3")
    @Expose
    private String ingredient3;

    public String getName() {
        return name;
    }

    public String getInstructions() {
        return instructions;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public String getIngredient1() {
        return ingredient1;
    }

    public String getIngredient2() {
        return ingredient2;
    }

    public String getIngredient3() {
        return ingredient3;
    }


}
