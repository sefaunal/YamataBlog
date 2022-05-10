package com.example.yamatablog.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yamatablog.Models.Post;
import com.example.yamatablog.PostDetailsActivity;
import com.example.yamatablog.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context mContext;
    List<Post> mData;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.MyViewHolder holder, int position) {
        holder.postTitle.setText(mData.get(position).getPostTitle());
        holder.postDescription.setText(mData.get(position).getPostDescription());
        holder.postDate.setText(ServerValueToStringDate((long) mData.get(position).getPostCreateDate()) + " tarihinde oluşturuldu");
        Glide.with(mContext).load(mData.get(position).getPostPicture()).into(holder.postImage);

    }

    @Override
    public int getItemCount() { return mData.size(); }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView postTitle, postDescription, postDate;
        ImageView postImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            postTitle = itemView.findViewById(R.id.row_rating_name);
            postDescription = itemView.findViewById(R.id.row_rating_comment);
            postImage = itemView.findViewById(R.id.row_rating_img);
            postDate = itemView.findViewById(R.id.row_rating_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent postDetailsActivity = new Intent(mContext, PostDetailsActivity.class);
                    int position = getAdapterPosition();
                    postDetailsActivity.putExtra("postTitle", mData.get(position).getPostTitle());
                    postDetailsActivity.putExtra("postImage", mData.get(position).getPostPicture());
                    postDetailsActivity.putExtra("postDescription", mData.get(position).getPostDescription());
                    postDetailsActivity.putExtra("postID", mData.get(position).getPostID());
                    postDetailsActivity.putExtra("postAddress", mData.get(position).getPostAddress());
                    postDetailsActivity.putExtra("postDepartment", mData.get(position).getPostAssign());
                    postDetailsActivity.putExtra("userID",mData.get(position).getUserID());
                    postDetailsActivity.putExtra("postStatus",mData.get(position).getPostStatus());
                    postDetailsActivity.putExtra("postFixedBy",mData.get(position).getPostFixedBy());
                    postDetailsActivity.putExtra("userRated", mData.get(position).getUserRated());

                    long createDate = (long) mData.get(position).getPostCreateDate();
                    long fixDate = (long) mData.get(position).getPostFixDate();

                    postDetailsActivity.putExtra("postCreateDate",createDate);
                    postDetailsActivity.putExtra("postFixDate",fixDate);

                    mContext.startActivity(postDetailsActivity);
                }
            });
        }
    }

    private String ServerValueToStringDate(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy",calendar).toString();
        return date;
    }
}
