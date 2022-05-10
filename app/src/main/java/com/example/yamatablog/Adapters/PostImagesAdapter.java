package com.example.yamatablog.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yamatablog.FullScreenImage;
import com.example.yamatablog.Models.PostImages;
import com.example.yamatablog.R;

import java.util.List;

public class PostImagesAdapter extends RecyclerView.Adapter<PostImagesAdapter.MyImageHolder> {

    Context mContext;
    List<String> mData;

    public PostImagesAdapter(Context mContext, List<String> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.post_image_rv, parent, false);
        return new MyImageHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyImageHolder holder, int position) {
        Glide.with(mContext).load(mData.get(position)).into(holder.postImage);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyImageHolder extends RecyclerView.ViewHolder{

        ImageView postImage;
        public MyImageHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.post_image_rv_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent fullScreenActivity = new Intent(mContext, FullScreenImage.class);
                    int position = getAdapterPosition();
                    fullScreenActivity.putExtra("ImageLink", mData.get(position));
                    mContext.startActivity(fullScreenActivity);
                }
            });
        }
    }
}
