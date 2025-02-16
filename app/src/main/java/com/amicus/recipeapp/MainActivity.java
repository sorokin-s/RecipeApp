package com.amicus.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
   Button createRecipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        createRecipe = findViewById(R.id.createButton);

        createRecipe.setOnClickListener(b->{
            Intent intent = new Intent(this, CreateRecipeActivity.class);
            startActivity(intent);
        });

    }

}