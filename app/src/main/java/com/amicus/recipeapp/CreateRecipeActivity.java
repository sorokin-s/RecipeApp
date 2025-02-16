package com.amicus.recipeapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateRecipeActivity extends AppCompatActivity {

    private static final int YOUR_RESULT_CODE = 1;

    EditText recipeName;
    EditText recipeInstructions;
    EditText recipeIngredients;
    Boolean blinker=false; // блинкер разрешения редактирования рецепта
    ImageView recipeImage;
    TextView editRecipe, addImage;
    Button downloadButton;
    Button createManualButton;
    Button saveButton;
    LinearLayout layout;
    AppDataBase db;
    DaoRecipe daoRecipe;
    String imageUrl;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101)if(resultCode==RESULT_OK) {
            assert data != null;
            recipeImage.setImageURI(data.getData());
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        db = AppDataBase.getInstance(this);
        daoRecipe = db.daoRecipe();
        recipeName = findViewById(R.id.recipeName);
        recipeInstructions = findViewById(R.id.recipeInstructions);
        recipeIngredients = findViewById(R.id.recipeIngredients);
        recipeImage = findViewById(R.id.recipeImage);
        downloadButton = findViewById(R.id.downloadButton);
        createManualButton = findViewById(R.id.createManualButton);
        saveButton = findViewById(R.id.saveButton);
        editRecipe = findViewById(R.id.editRecipe);
        addImage = findViewById(R.id.addImage);
        editRecipe.requestFocus();
        layout = findViewById(R.id.linerLayout);
        layout.setVisibility(View.GONE);
        addImage.setVisibility(View.GONE);
        editRecipe.setOnClickListener(b-> {
                blinker = !blinker;
                recipeName.setClickable(blinker);
                recipeName.setFocusable(blinker);
                recipeName.setCursorVisible(blinker);
                recipeName.setFocusableInTouchMode(blinker);
                recipeIngredients.setClickable(blinker);
                recipeIngredients.setFocusable(blinker);
                recipeIngredients.setCursorVisible(blinker);
                recipeIngredients.setFocusableInTouchMode(blinker);
                recipeInstructions.setClickable(blinker);
                recipeInstructions.setFocusable(blinker);
                recipeInstructions.setCursorVisible(blinker);
                recipeInstructions.setFocusableInTouchMode(blinker);
                if(!blinker){
                    editRecipe.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(editRecipe.getWindowToken(), 0);
                        editRecipe.setText(R.string.editRecipe);
                        addImage.setVisibility(View.GONE);
                    }
                }else{
                    recipeName.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(recipeName, InputMethodManager.SHOW_IMPLICIT);
                    }
                    addImage.setVisibility(View.VISIBLE);
                    editRecipe.setText("Закончить редактирование");
                }
            //editRecipe.append(blinker.toString());
        });

        addImage.setOnClickListener(b->{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                    + "/storage/");
            intent.setDataAndType(uri, "*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);


            startActivityForResult(intent,101);
        });


        createManualButton.setOnClickListener(b->{
            layout.setVisibility(View.VISIBLE);
            addImage.setVisibility(View.VISIBLE);
            recipeImage.setImageResource(0);
            recipeName.setText("");
            recipeIngredients.setText("");
            recipeInstructions.setText("");
            editRecipe.callOnClick();
        });

        downloadButton.setOnClickListener(b->{
            addImage.setVisibility(View.GONE);
                if(blinker){
                    editRecipe.callOnClick();

                }
                layout.setVisibility(View.VISIBLE);
                fetchRandomRecipe();
        });


        saveButton.setOnClickListener(b->{
            String nameFile = recipeName.getText()+".jpg";
            Executors.newSingleThreadExecutor().execute(()->{
                daoRecipe.insert(new Meal(nameFile,
                        String.valueOf(recipeName.getText()),
                        String.valueOf(recipeIngredients.getText()),
                        String.valueOf(recipeInstructions.getText())));
            });
            FileHelper.saveToFile(nameFile,recipeImage,this);
        });
    }
    private static File getFilePath(String fileName, Context context){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            return new  File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),fileName);
        }else return  new File(Environment.getExternalStorageDirectory(),fileName);
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
                    Meal meal = response.body().getMeals().get(0);  //присваеваем из полученного списка первый и единственный элемент
                    recipeName.setText(meal.getMealName());
                    recipeInstructions.setText(meal.getInstructions());

                    String ingredients = getString(R.string.ingredients)+meal.getIngredient1() +", "+meal.getIngredient2()
                            + ", "+ meal.getIngredient3()+ ", "+meal.getIngredient4() +", "+meal.getIngredient5()
                            + ", "+ meal.getIngredient6()+ ", "+ meal.getIngredient7();
                    recipeIngredients.setText(ingredients);

                    Glide.with(CreateRecipeActivity.this).load(meal.getImageUrl()).into(recipeImage); // устанавливаем изображение в ImageView
                    imageUrl=meal.getImageUrl();
                    Toast.makeText(CreateRecipeActivity.this, R.string.successDownloadsRecipes, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(CreateRecipeActivity.this, R.string.errorDownloadsRecipe, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<RecipeResponce> call, Throwable t) {
                Toast.makeText(CreateRecipeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private Intent createProcessTextIntent() {
        return new Intent()
                .setAction(Intent.ACTION_PROCESS_TEXT)
                .setType("text/plain");
    }
    private List getSupportedActivities(TextView textView) {
        PackageManager packageManager =
                textView.getContext().getPackageManager();
        return
                packageManager.queryIntentActivities(createProcessTextIntent(),
                        0);
    }
}