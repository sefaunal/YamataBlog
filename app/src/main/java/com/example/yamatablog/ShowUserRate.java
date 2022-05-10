package com.example.yamatablog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yamatablog.Adapters.RatingAdapter;
import com.example.yamatablog.Models.UserRate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowUserRate extends AppCompatActivity {
    ImageView userImage;
    TextView userName, userLastSeen;
    String UserID;
    List<UserRate>rateList;
    RatingAdapter rateAdapter;

    RecyclerView userRateRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_rate);
        getSupportActionBar().hide();

        userRateRV = findViewById(R.id.show_user_rate_rv);
        userRateRV.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        userRateRV.setLayoutManager(linearLayoutManager);

        userImage = findViewById(R.id.show_user_rate_image);
        userName = findViewById(R.id.show_user_rate_name);
        userLastSeen = findViewById(R.id.show_user_rate_status);

        Glide.with(this).load(getIntent().getExtras().getString("UserImage")).into(userImage);
        userName.setText(getIntent().getExtras().getString("UserName"));
        userLastSeen.setText(getIntent().getExtras().getString("UserLastSeen"));
        UserID = getIntent().getExtras().getString("UserID");

        DatabaseReference rRef = FirebaseDatabase.getInstance().getReference("UserRatings").child(UserID);
        rRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rateList = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()){
                    UserRate userRate = snap.getValue(UserRate.class);
                    rateList.add(userRate);
                }
                rateAdapter = new RatingAdapter(getApplicationContext(), rateList);
                userRateRV.setAdapter(rateAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}