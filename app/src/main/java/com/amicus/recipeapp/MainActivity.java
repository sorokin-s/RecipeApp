package com.amicus.recipeapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    TextView recipeName;
    TextView recipeInstructions;
    TextView recipeIngredients;

    ImageView recipeImage;

    Button refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recipeName = findViewById(R.id.recipeName);
        recipeInstructions = findViewById(R.id.recipeInstructions);
        recipeIngredients = findViewById(R.id.recipeIngredients);
        recipeImage = findViewById(R.id.recipeimage);
        refreshButton = findViewById(R.id.refreshButton);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchRandomRecipe();
            }
        });
        fetchRandomRecipe();
    }

    private void fetchRandomRecipe() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.themealdb.com/api/json/v1/1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RecipeApi recipeApi = retrofit.create(RecipeApi.class);
        recipeApi.getRandomRecipe().enqueue(new Callback<RecipeResponce>() {
            @Override
            public void onResponse(Call<RecipeResponce> call, Response<RecipeResponce> response) {
                if (response.isSuccessful()&& response.body() != null){
                    Meal meal = response.body().getMeals().get(0);
                    recipeName.setText(meal.getName());
                    recipeInstructions.setText(meal.getInstructions());

                    String ingredients = meal.getIngredient1() +"\n"+meal.getIngredient2()
                            + "\n"+ meal.getIngredient3();
                    recipeIngredients.setText(ingredients);

                    Glide.with(MainActivity.this).load(meal.getImageUrl()).into(recipeImage);
                    Toast.makeText(MainActivity.this, R.string.successDownloadsRecipes, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, R.string.errorDownloadsRecipe, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<RecipeResponce> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}