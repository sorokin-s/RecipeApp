package com.amicus.recipeapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateRecipeActivity extends AppCompatActivity {

    private static final int YOUR_RESULT_CODE = 1;
    private static int id;
    private static boolean loadFromDb;
    EditText recipeName;
    EditText recipeInstructions;
    EditText recipeIngredients;
    Boolean blinker=false; //  разрешения редактирования рецепта
    Boolean blkEdited =false; // признак попытки редактирования згр. из БД рецепта
    ImageView recipeImage;
    TextView  btnAddImageFromStorage, btnTranslate;// выполняют функцию кнопки
    Button btnEditRecipe;
    Button btnDownload;
    Button btnCreateManual;
    Button btnSaveInDB,btnDeleteFromDB;
    ConstraintLayout mainLayout;
    AppDataBase db;
    DaoRecipe daoRecipe;
    String imageUrl;
    ScrollView scrollView;
    LinearLayout layoutBtnTop;
    TranslationHelper translationHelper;

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

        mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.main6));
        db = AppDataBase.getInstance(this);
        daoRecipe = db.daoRecipe();
        recipeName = findViewById(R.id.recipeName);
        recipeInstructions = findViewById(R.id.recipeInstructions);
        recipeIngredients = findViewById(R.id.recipeIngredients);
        recipeImage = findViewById(R.id.recipeImage);
        btnDownload = findViewById(R.id.downloadButton);
        btnCreateManual = findViewById(R.id.createManualButton);
        btnSaveInDB = findViewById(R.id.saveButton);
        btnDeleteFromDB = findViewById(R.id.deleteButton);
        btnEditRecipe = findViewById(R.id.editRecipe);
        btnAddImageFromStorage = findViewById(R.id.addImage);
        btnTranslate = findViewById(R.id.btnTranslate);
        btnEditRecipe.requestFocus();
        scrollView = findViewById(R.id.scrollView);
        layoutBtnTop = findViewById(R.id.linearLayout);
        /////////////////////////////////////////////////////////////////////////////////
        initializeView();
        //////////////////////////////////////////////////////////////////////////////////
        btnEditRecipe.setOnClickListener(b-> {  // кнопка вкл/откл редактирования рецепта
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
                if(!blinker){         //
                    btnEditRecipe.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(btnEditRecipe.getWindowToken(), 0);
                        btnEditRecipe.setText(R.string.editRecipe);
                        btnAddImageFromStorage.setVisibility(View.GONE);
                    }
                }else{
                    recipeName.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(recipeName, InputMethodManager.SHOW_IMPLICIT);
                    }
                    btnAddImageFromStorage.setVisibility(View.VISIBLE);
                    btnEditRecipe.setText("Закончить редактирование");
                }
            blkEdited=true;
        });
        translationHelper = new TranslationHelper();
        translationHelper.downloadModel();
        translationHelper.addLister(event->translateText( event.getNewValue()));
        btnTranslate.setOnClickListener(b->{           // кнопка перевода текста с исходного языка
            String[]texts ={String.valueOf(recipeName.getText()),
            String.valueOf(recipeIngredients.getText()),
            String.valueOf(recipeInstructions.getText())};
            translationHelper
                    .translateText(texts);
            Log.d("setOnClickListener ", String.join(",",texts));
        });

        btnAddImageFromStorage.setOnClickListener(b->{           // установка изображения для рецепта из хранилища(ssda,storage)
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                    + "/storage/");
            intent.setDataAndType(uri, "*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent,101);
        });


        btnCreateManual.setOnClickListener(b->{         // создание нового рецепта
            scrollView.setVisibility(View.VISIBLE);
            btnAddImageFromStorage.setVisibility(View.VISIBLE);
            btnEditRecipe.setVisibility(View.VISIBLE);
            recipeImage.setImageResource(0);
            recipeName.setText("");
            recipeIngredients.setText("");
            recipeInstructions.setText("");
            btnEditRecipe.callOnClick();
           scrollView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.main3));
            btnSaveInDB.setVisibility(View.VISIBLE);
        });

        btnDownload.setOnClickListener(b->{       // загрузка рецепта из Internet
          scrollView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.main3));
            btnSaveInDB.setVisibility(View.VISIBLE);
            btnEditRecipe.setVisibility(View.VISIBLE);
            btnAddImageFromStorage.setVisibility(View.GONE);
                if(blinker){
                    btnEditRecipe.callOnClick();

                }
                scrollView.setVisibility(View.VISIBLE);
                fetchRandomRecipe();

        });


        btnSaveInDB.setOnClickListener(b->{                   // сохранение Рецепта
            String nameFile = recipeName.getText()+".jpg";
            if(blkEdited&&loadFromDb){   // если сохраняем загруженный из БД рецепт
                Executors.newSingleThreadExecutor().execute(()->{ // отправляем запрос в БД для записи значений
                    daoRecipe.updateByID(id,
                            nameFile,
                            String.valueOf(recipeName.getText()),
                            String.valueOf(recipeIngredients.getText()),
                            String.valueOf(recipeInstructions.getText()));

                });
                FileHelper.saveToFile(nameFile,recipeImage,this);// сохраняем на диске изображение из ImageView c новым именем
            }else{
                Executors.newSingleThreadExecutor().execute(()->{ // отправляем запрос в БД для записи значений
                    daoRecipe.insert(new Meal(nameFile,    //имя файлу изображения в БД
                            String.valueOf(recipeName.getText()),
                            String.valueOf(recipeIngredients.getText()),
                            String.valueOf(recipeInstructions.getText())));

                });
                FileHelper.saveToFile(nameFile,recipeImage,this);// сохраняем на диске изображение из ImageView c новым именем
            }

            Intent intent = new Intent();
            setResult(RESULT_OK, intent); // отправляем код результата в 1 активити для обработки, без данных в данном случае
            finish();
        });

        btnDeleteFromDB.setOnClickListener(b->{
            deleteDialog();    //вызываем диалог удаления рецепта из БД
        });
        if(loadFromDb)loadRecipeFromDb(id);


    }
   public void translateText(Object _texts){
       ArrayList<String> texts = (ArrayList<String>) _texts;
     Log.d("translateText ",String.join(" ,",texts));
       recipeName.setText(texts.get(0));
      Log.d("translateText ",texts.get(0));
       recipeIngredients.setText(texts.get(1));
       recipeInstructions.setText(texts.get(2));
    }
    public void initializeView(){ // инициализация элементов активити в зависимости от задачи
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            id = extras.getInt(MainActivity.ID_KEY,0);
            loadFromDb = extras.getBoolean(MainActivity.LOAD_FROM_DB,false);
        }
        if(!loadFromDb){  // false - вид активити для создания или загрузки рецепта из интернета
            scrollView.setVisibility(View.GONE);
            btnAddImageFromStorage.setVisibility(View.GONE);
            btnSaveInDB.setVisibility(View.GONE);
            btnDeleteFromDB.setVisibility(View.GONE);
            btnEditRecipe.setVisibility(View.GONE);

        }else{// true - вид активити для отображения рецепта из БД для просмотра и редактирования

            scrollView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.main3));
            layoutBtnTop.setVisibility(View.GONE);
            btnAddImageFromStorage.setVisibility(View.GONE);
            btnDeleteFromDB.setVisibility(View.VISIBLE);

        }

    }
    @Override
    protected void onDestroy() {
//        Intent intent = new Intent(this,MainActivity.class);
//        intent.putExtra(MainActivity.LOAD_FROM_DB,false);
//        setResult(RESULT_OK,intent);
        super.onDestroy();
    }
    private void loadRecipeFromDb(int idRecipe){
        Executors.newSingleThreadExecutor().execute(()-> {
            try{
            Meal meal = daoRecipe.getRecipeById(idRecipe);
            Log.d("Complete","meal");
            File file = FileHelper.getFilePath(meal.getImageUrl(), this);
            Log.d("Complete"+id,"Uri");
            recipeImage.setImageURI(Uri.parse(file.getPath()));
            Log.d("Complete"+id,"image");
            recipeName.setText(meal.getMealName()); // ERROR!!! Can't create handler inside thread Thread[pool-13-thread-1,5,main] that has not called Looper.prepare()
             Log.d("Complete"+id,"name");
            recipeIngredients.setText(meal.getIngredient1());
             Log.d("Complete"+id,"ingredient");
            recipeInstructions.setText( meal.getInstructions());
             Log.d("Complete"+id,"instructions");
            } catch(Exception e){Log.d("Exeption",e.getMessage());}

        });

    }

    private void fetchRandomRecipe() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.themealdb.com/api/json/v1/1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RecipeApi recipeApi = retrofit.create(RecipeApi.class);
        recipeApi.getRandomRecipe().enqueue(new Callback<RecipeResponce>() {
            @SuppressLint("SetTextI18n")
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
    public void deleteDialog(){              // диалог удаления элементов списка
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setMessage("Удалить рецепт "+recipeName.getText()+" ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Executors.newSingleThreadExecutor().execute(()->{
                    daoRecipe.deleteByID(id);// отправляем запрос в БД для удаления
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent); // отправляем код результата в 1 активити для обработки, без данных в данном случае
                    finish();
                });
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}