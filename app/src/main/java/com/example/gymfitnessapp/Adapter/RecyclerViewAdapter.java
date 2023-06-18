package com.example.gymfitnessapp.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.gymfitnessapp.Interface.Model.Exercise;
import com.example.gymfitnessapp.R;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Exercise> exerciseList;
    private OnItemClickListener listener;
    private RequestManager glideRequestManager;
    public RecyclerViewAdapter(Context context, List<Exercise> exerciseList) {
        this.context = context;
        this.exerciseList = exerciseList;
    }
    public interface OnItemClickListener {
        void onItemClick(Exercise exercise);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);
        Log.d(this.getClass().getSimpleName(), "GIF: " + exercise.getGifUrl());
        holder.txtExerciseName.setText(exercise.getName());

        // Sử dụng Glide để tải hình ảnh GIF từ URL và hiển thị trong ImageView
        Glide.with(context).load(exercise.getGifUrl())
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .error(R.drawable.logo)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(holder.imgExerciseGif);

        // Inside onBindViewHolder, set click listener for each item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(exercise);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtExerciseName;
        GifImageView imgExerciseGif;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtExerciseName = itemView.findViewById(R.id.ex_name);
            imgExerciseGif = itemView.findViewById(R.id.ex_img);
        }
    }
}
