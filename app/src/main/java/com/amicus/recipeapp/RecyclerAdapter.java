package com.amicus.recipeapp;

import android.annotation.SuppressLint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    List<Item> items;
    View view;
    public interface OnItemClickListener{// слушатель
        void onItemClick(Item recipe,int position,View itemView);
    }
    private final OnItemClickListener onItemClickListener;
    public RecyclerAdapter(List<Item> items, OnItemClickListener listener) { // конструктор
        this.items = items;
        this.onItemClickListener = listener;
    }
    ViewGroup viewGroup;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);
     viewGroup = parent;
       return  new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      Item currentItem = items.get(position);

       File file = FileHelper.getFilePath(currentItem.getImageUrl(),view.getContext());

        Uri uri = Uri.parse(file.getPath());
        try {
            holder.imageView.setImageURI(uri);
        } catch (Exception e) {
            Log.d("uri_Error: "+uri,e.getMessage());
        }
        holder.textView1.setText(currentItem.getMealName());
        holder.textView2.setText("id "+currentItem.getId());
        holder.itemView.setOnClickListener(i->{   // обработка клика на элементе
            onItemClickListener.onItemClick(currentItem,position,holder.itemView); // передаём данные в метод слушателя
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public void update(){this.notifyDataSetChanged();}
    public  class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView1,textView2;
        ImageView imageView;
        CheckBox checkBox;
        public ViewHolder(@NotNull View itemView){ // конструктор
            super(itemView);
            textView1=itemView.findViewById(R.id.itemTextViewName);
            textView2=itemView.findViewById(R.id.itemTextViewId);
            imageView = itemView.findViewById(R.id.image);
        }


    }
}
