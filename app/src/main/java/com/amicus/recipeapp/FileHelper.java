package com.amicus.recipeapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.Objects;

public class FileHelper {
     static File getFilePath(String fileName, Context context){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            return new  File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),fileName);
        }else return  new File(Environment.getExternalStorageDirectory(),fileName);
    }

    public static void saveToFile(String fileName, ImageView iv, Context context){
        File file = getFilePath(fileName,context);
        try(OutputStream outputStream = new FileOutputStream(file)){
            BitmapDrawable bitmap = (BitmapDrawable) iv.getDrawable();
            bitmap.getBitmap().compress(Bitmap.CompressFormat.JPEG, 70, outputStream);// записываем в файл с сжатием 70%
           // Log.d("saveError: ", );
        } catch (Exception e) {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
            Log.d("saveError: ", Objects.requireNonNull(e.getMessage()));
        }

    }
    public static String loadFile(String filename, Context context){
        File file = getFilePath(filename,context);
        if(!file.exists())return "couldn't find file";
       // String
        return "";
    }
    public  static  boolean deleteFile(String filename, Context context){
        File file = getFilePath(filename,context);
        return file.delete();
    }
}
