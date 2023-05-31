package com.example.gymfitnessapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gymfitnessapp.Model.Exercise;
import com.example.gymfitnessapp.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Exercise> exerciseList;

    public RecyclerViewAdapter(Context context, List<Exercise> exerciseList) {
        this.context = context;
        this.exerciseList = exerciseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);

        holder.txtExerciseName.setText(exercise.getName());

        Glide.with(context)
                .asGif()
                .load(exercise.getGifUrl())
                .into(holder.imgExerciseGif);
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtExerciseName;
        ImageView imgExerciseGif;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtExerciseName = itemView.findViewById(R.id.ex_name);
            imgExerciseGif = itemView.findViewById(R.id.ex_img);
        }
    }
}

