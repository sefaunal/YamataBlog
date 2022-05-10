package com.example.yamatablog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();

        //Check if user already signed in
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Thread splash = new Thread(){
            public void run(){
                try {
                    sleep(1500);
                }
                catch (InterruptedException e){
                    //Show Message
                }
                finally {
                    if (firebaseUser != null)
                        startActivity(new Intent(MainActivity.this, HomeActivity.class ));
                    else
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        splash.start();
    }
}