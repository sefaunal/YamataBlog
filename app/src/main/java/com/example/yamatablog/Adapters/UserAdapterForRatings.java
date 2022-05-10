package com.example.yamatablog.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yamatablog.MessageActivity;
import com.example.yamatablog.Models.UserDetails;
import com.example.yamatablog.R;
import com.example.yamatablog.ShowUserRate;

import java.util.List;

public class UserAdapterForRatings extends RecyclerView.Adapter<UserAdapterForRatings.UserViewHolder> {

    private Context mContext;
    private List<UserDetails> mData;

    public UserAdapterForRatings(Context mContext, List<UserDetails> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public UserAdapterForRatings.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_message_user, parent, false);

        return new UserAdapterForRatings.UserViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapterForRatings.UserViewHolder holder, int position) {
        UserDetails user = mData.get(position);

        holder.userName.setText(user.getUserName());
        Glide.with(mContext).load(user.getUserPhoto()).into(holder.userImage);
        if (user.getUserStatus().equals("online")){
            holder.userOnline.setVisibility(View.VISIBLE);
            holder.userOffline.setVisibility(View.INVISIBLE);
        }
        else{
            holder.userOnline.setVisibility(View.INVISIBLE);
            holder.userOffline.setVisibility(View.VISIBLE);
        }

        holder.LastMessage.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        ImageView userImage, userOnline, userOffline;
        TextView userName, LastMessage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.message_user_image);
            userName = itemView.findViewById(R.id.message_user_name);
            userOnline = itemView.findViewById(R.id.message_user_on);
            userOffline = itemView.findViewById(R.id.message_user_off);
            LastMessage = itemView.findViewById(R.id.message_last_message);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Intent rateIntent = new Intent(mContext, ShowUserRate.class);
                    rateIntent.putExtra("UserID", mData.get(position).getUserID());
                    rateIntent.putExtra("UserName", mData.get(position).getUserName());
                    rateIntent.putExtra("UserLastSeen", mData.get(position).getUserLastSeen());
                    rateIntent.putExtra("UserImage", mData.get(position).getUserPhoto());
                    mContext.startActivity(rateIntent);
                }
            });
        }
    }
}
