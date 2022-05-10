package com.example.yamatablog.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yamatablog.R;

import java.util.List;

public class PopupImageAdapter extends RecyclerView.Adapter<PopupImageAdapter.ImageViewHolder> {

    Context mContext;
    List<Uri> mData;

    public PopupImageAdapter(Context mContext, List<Uri> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.post_image_rv, parent, false);
        return new ImageViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.postImage.setImageURI(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        ImageView postImage;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.post_image_rv_image);
        }
    }
}
