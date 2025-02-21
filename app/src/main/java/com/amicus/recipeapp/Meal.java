
package com.amicus.recipeapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Entity(tableName = "recipesDB")
public class Meal {
    @PrimaryKey(autoGenerate = true)
    public int id;    //0
    @SerializedName("strMeal")
    @Expose
    private String mealName;//1

    @SerializedName("strMealThumb")
    @Expose
    private String imageUrl;//2
    @SerializedName("strIngredient1")
    @Expose
    private String ingredient1; //3
    @SerializedName("strIngredient2")
    @Expose
    private String ingredient2;//4
    @SerializedName("strIngredient3")
    @Expose
    private String ingredient3;//5

    @SerializedName("strIngredient4")
    @Expose
    private String ingredient4;//6
    @SerializedName("strIngredient5")
    @Expose
    private String ingredient5;//7
    @SerializedName("strIngredient6")
    @Expose
    private String ingredient6;//8

    @SerializedName("strIngredient7")
    @Expose
    private String ingredient7;//9
    @SerializedName("strInstructions")
    @Expose
    private String instructions;//10
    public String getIngredient7() {
        return ingredient7;
    }

    public void setIngredient7(String ingredient7) {
        this.ingredient7 = ingredient7;
    }

    public String getIngredient6() {
        return ingredient6;
    }

    public Meal(String imageUrl,String mealName, String ingredient1, String instructions) {
        this.imageUrl = imageUrl;
        this.mealName = mealName;
        this.ingredient1 = ingredient1;
        this.instructions = instructions;

    }

    public void setIngredient6(String ingredient6) {
        this.ingredient6 = ingredient6;
    }

    public String getIngredient5() {
        return ingredient5;
    }

    public void setIngredient5(String ingredient5) {
        this.ingredient5 = ingredient5;
    }

    public String getIngredient4() {
        return ingredient4;
    }

    public void setIngredient4(String ingredient4) {
        this.ingredient4 = ingredient4;
    }
    public String getMealName() {
        return mealName;
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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setIngredient1(String ingredient1) {
        this.ingredient1 = ingredient1;
    }

    public void setIngredient2(String ingredient2) {
        this.ingredient2 = ingredient2;
    }

    public void setIngredient3(String ingredient3) {
        this.ingredient3 = ingredient3;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getIngredient2() {
        return ingredient2;
    }

    public String getIngredient3() {
        return ingredient3;
    }


}
