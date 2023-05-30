package com.example.gymfitnessapp.Adapter;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gymfitnessapp.Interface.ItemClickListener;
import com.example.gymfitnessapp.Model.Exercise;
import com.example.gymfitnessapp.R;
import com.example.gymfitnessapp.ViewPosture;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textView;
    public GifImageView gifImageView;

    private ItemClickListener itemClickListener;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.ex_name);
        gifImageView = itemView.findViewById(R.id.ex_img);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition());
    }
}

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private List<Exercise> exerciseList;
    private Context context;

    public RecyclerViewAdapter(List<Exercise> exerciseList, Context context) {
        this.exerciseList = exerciseList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_exercise, viewGroup, false);

        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int position) {
        Exercise exercise = exerciseList.get(position);
        recyclerViewHolder.gifImageView.setImageURI(Uri.parse((exercise.getGifUrl())));
        recyclerViewHolder.textView.setText(exercise.getName());
        recyclerViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                // Start new Activity
                Intent intent = new Intent(context, ViewPosture.class);
                intent.putExtra("image_url", exercise.getGifUrl());

                intent.putExtra("name", exercise.getName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }
}
