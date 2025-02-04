package com.amicus.recipeapp;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeApi {
    @GET("random.php")
    Call<RecipeResponce> getRandomRecipe();
}
