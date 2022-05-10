package com.example.yamatablog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class FullScreenImage extends AppCompatActivity {

    ImageView fullscreen;
    String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        Objects.requireNonNull(getSupportActionBar()).hide();


        fullscreen = findViewById(R.id.fullscreen_image);
        link = getIntent().getExtras().getString("ImageLink");
        Glide.with(this).load(link).into(fullscreen);


    }

}