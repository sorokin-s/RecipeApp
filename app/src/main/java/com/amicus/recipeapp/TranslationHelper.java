package com.amicus.recipeapp;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TranslationHelper {

    public static boolean downloadsModel;
    public   ArrayList<String> listTransl = new ArrayList<>();
    PropertyChangeSupport psc = new PropertyChangeSupport(this);
    public void addLister(PropertyChangeListener listner){
        psc.addPropertyChangeListener(listner);
    }
    TranslatorOptions options =
            new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.RUSSIAN)
                    .build();
    final  Translator englishRussianTranslator=
            Translation.getClient(options);


   int i =0;
    public  void  translateText(String[] strings) {
        ArrayList<String> list= new ArrayList<>();
        for(String str:strings){
           englishRussianTranslator
                   .translate(str)
                   .addOnSuccessListener(
                    new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                          //  Log.d("TRANSLATOR> ",String.valueOf(o));
                            list.add(String.valueOf(o));
                            i++;
                            if(i>2){
                                Log.d("setListTranslated TRANSLATOR> ",list.toString());
                                setList(list);
                                i=0;
                            }
                        }

                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TRANSLATOR> ","TRANSLATOR ERROr!!!!!!!!!!!!!!!!!!!!!!!!!!"); // Error.
                                    // ...
                                }
                            });


        }

    }




    public void setList(ArrayList<String> newList) {

        ArrayList<String> oldList =this.listTransl;
        this.listTransl = newList;
        psc.firePropertyChange("Translated",oldList,newList);
        listTransl.clear();
    }



    public static void downloadModel() {
        RemoteModelManager modelManager = RemoteModelManager.getInstance();

// Get translation models stored on the device.
        modelManager.getDownloadedModels(TranslateRemoteModel.class)
                .addOnSuccessListener(new OnSuccessListener<Set>() {
                    @Override
                    public void onSuccess(Set models) {
                        // ...
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error.
                    }
                });
        // Download the Russian model.
        TranslateRemoteModel englishModel =
                new TranslateRemoteModel.Builder(TranslateLanguage.ENGLISH).build();
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        modelManager.download(englishModel, conditions)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        downloadsModel = true;

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        downloadsModel = false;   // Error.
                    }
                });
        TranslateRemoteModel russianModel =
                new TranslateRemoteModel.Builder(TranslateLanguage.RUSSIAN).build();
        DownloadConditions conditions1 = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        modelManager.download(russianModel, conditions1)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        downloadsModel = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        downloadsModel = false;   // Error.
                    }
                });

    }





}
