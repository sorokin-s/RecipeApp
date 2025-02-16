package com.amicus.recipeapp;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = {Meal.class},version = 1)
public abstract class AppDataBase extends RoomDatabase {
public abstract DaoRecipe daoRecipe();
private  static AppDataBase instance;
    public static synchronized AppDataBase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDataBase.class, "recipesDB")
                    .fallbackToDestructiveMigration()
                    .build();

        }
        return instance;
    }
}
