package com.example.yamatablog.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yamatablog.Models.Comment;
import com.example.yamatablog.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mData;

    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_comment, parent, false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, int position) {
        String UserID = mData.get(position).getUserID();
        DatabaseReference commentUserRef = FirebaseDatabase.getInstance().getReference("UserDetails").child(UserID);

        holder.commentContent.setText(mData.get(position).getContent());
        holder.commentDate.setText(ServerValueToTime((long)mData.get(position).getCommentTime()));

        commentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.commentUserName.setText(snapshot.child("userName").getValue(String.class));
                Glide.with(mContext).load(snapshot.child("userPhoto").getValue(String.class)).into(holder.commentUserImg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        ImageView commentUserImg;
        TextView commentContent, commentUserName, commentDate;

        public CommentViewHolder(View itemView){
            super(itemView);
            commentUserImg = itemView.findViewById(R.id.comment_user_img);
            commentUserName = itemView.findViewById(R.id.comment_username);
            commentContent = itemView.findViewById(R.id.comment_content);
            commentDate = itemView.findViewById(R.id.comment_date);
        }

    }

    private String ServerValueToTime(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("HH:mm",calendar).toString();
        return date;
    }
}
