package com.example.yamatablog.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yamatablog.Models.UserRate;
import com.example.yamatablog.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {

    private Context mContext;
    private List<UserRate> mData;

    public RatingAdapter(Context mContext, List<UserRate> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public RatingAdapter.RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_ratings, parent, false);
        return new RatingViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingAdapter.RatingViewHolder holder, int position) {
        holder.RateComment.setText(mData.get(position).getRateComment());
        holder.RateBar.setRating(mData.get(position).getRatePoint());
        holder.RateDate.setText(ServerValueToStringDate((long)mData.get(position).getRateTime()));

        String UserID = mData.get(position).getRatedByID();
        DatabaseReference uRef = FirebaseDatabase.getInstance().getReference("UserDetails").child(UserID);
        uRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.UserName.setText(snapshot.child("userName").getValue(String.class));
                Glide.with(mContext).load(snapshot.child("userPhoto").getValue(String.class)).into(holder.UserImage);
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

    public class RatingViewHolder extends RecyclerView.ViewHolder{
        RatingBar RateBar;
        TextView UserName, RateComment, RateDate;
        ImageView UserImage;


        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            RateComment = itemView.findViewById(R.id.row_rating_comment);
            RateBar = itemView.findViewById(R.id.row_rating_bar);
            UserName = itemView.findViewById(R.id.row_rating_name);
            UserImage = itemView.findViewById(R.id.row_rating_img);
            RateDate = itemView.findViewById(R.id.row_rating_date);
        }
    }

    private String ServerValueToStringDate(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy",calendar).toString();
        return date;
    }
}
