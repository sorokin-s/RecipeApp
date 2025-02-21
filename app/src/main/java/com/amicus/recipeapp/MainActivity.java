package com.amicus.recipeapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
     static final String ID_KEY="ID_KEY";
     static final String LOAD_FROM_DB="OAD_FROM_DB";

    Boolean load;
    LinearLayout layout;
    Button btnCreateRecipe;
    Toolbar toolbar;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    TextView title;
    RecyclerAdapter.OnItemClickListener itemClickListener;
    ConstraintLayout mainConstraint;
    List<Item> itemsRecipe;
    LinearLayoutManager linearLayoutManager;
    AppDataBase db;
    DaoRecipe daoRecipe;
    TranslationHelper translationHelper;
    ActivityResultLauncher<Intent> startActivityFor_Result = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onActivityResult(ActivityResult result) { // после закрытия второй активити обрабатываем результат
                    if(result.getResultCode() == Activity.RESULT_OK){
//                        Intent intent = result.getData();
//                        assert intent != null;
//                        load = intent.getBooleanExtra(LOAD_FROM_DB,false);

                        itemsRecipe.clear();
                        fillItemsRecipe();
                        recyclerAdapter.notifyDataSetChanged();// обновляeм RecyclerView

                    }

                }
            });

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.main);

        mainConstraint = findViewById(R.id.mainConstraint);
        layout.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.main3));
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.title);
        db = AppDataBase.getInstance(this);
        daoRecipe = db.daoRecipe();
        itemsRecipe = new ArrayList<>();
        btnCreateRecipe = findViewById(R.id.createButton);
        recyclerView= findViewById(R.id.recipe_list);
        linearLayoutManager =new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        TranslationHelper.downloadModel();
        itemClickListener = new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item recipe, int position, View itemView) {
                itemSelected(recipe, position,itemView);
            }
        };
        recyclerAdapter = new RecyclerAdapter (itemsRecipe,itemClickListener);
        recyclerView.setAdapter(recyclerAdapter);
        btnCreateRecipe.setOnClickListener(b->{
            load=false; // false - активити для создания и загрузки из интернета
            Intent intent = new Intent(this, CreateRecipeActivity.class);
            intent.putExtra(ID_KEY,0);                                    //
            intent.putExtra(LOAD_FROM_DB,load);
            startActivityFor_Result.launch(intent);

        });

         setSupportActionBar(toolbar);
         getSupportActionBar().setDisplayShowTitleEnabled(false);


       fillItemsRecipe(); // заполняем список данными из БД

    }

    void itemSelected(Item recipe,int position,View itemView){
        load=true;  // true - активити для отображения рецепта из БД для просмотра и редактирования
        Intent intent = new Intent(this, CreateRecipeActivity.class);
        intent.putExtra(ID_KEY,recipe.getId()); //
        intent.putExtra(LOAD_FROM_DB,load);  //
        startActivityFor_Result.launch(intent);

    }
    @SuppressLint({"NotifyDataSetChanged", "ResourceAsColor"})
    void fillItemsRecipe(){          // заполняем список  данными  из БД
        Executors.newSingleThreadExecutor().execute(()-> {
            for(Meal b:daoRecipe.getAllRecipe()) {
                itemsRecipe.add(new Item(b.id, b.getImageUrl(), b.getMealName()));
            }
            title.setText("Мои рецепты");
            title.append("\nсохранено рецертов: "+itemsRecipe.size());

        });

    }
////////////toolBar//////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Toast.makeText(MainActivity.this,"Settings choice",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_exit:
                finish();
                return true;
            case R.id.about:
                Intent intent = new Intent(this,AboutActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}