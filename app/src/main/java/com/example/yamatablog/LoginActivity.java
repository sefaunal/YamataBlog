package com.example.yamatablog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText login_mail, login_password;
    TextView register, resetpassword;
    Button loginbtn;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        login_mail = findViewById(R.id.login_mail);
        login_password = findViewById(R.id.login_password);
        register = findViewById(R.id.login_register);
        resetpassword = findViewById(R.id.login_resetpassword);
        loginbtn = findViewById(R.id.login_button);

        loginbtn.setOnClickListener(view -> {login();});
        register.setOnClickListener(view -> {startActivity(new Intent(LoginActivity.this, RegisterActivity.class));});
        resetpassword.setOnClickListener(view -> {resetpassword();});
    }

    public void login(){
        if(TextUtils.isEmpty(login_mail.getText().toString())){
            login_mail.setError("Lütfen mail adresinizi girin");
            login_mail.requestFocus();
        }
        else if(TextUtils.isEmpty(login_password.getText().toString())){
            login_password.setError("Lütfen şifrenizi girin");
            login_password.requestFocus();
        }
        else{
            mAuth.signInWithEmailAndPassword(login_mail.getText().toString(), login_password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    }
                    else
                        Toast.makeText(LoginActivity.this, "Hata: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void resetpassword(){
        if(TextUtils.isEmpty(login_mail.getText().toString())){
            login_mail.setError("Şifrenizi sıfırlamak için lütfen mail adresinizi girin");
            login_mail.requestFocus();
        }
        else{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Şifre Sıfırlama");
            dialog.setMessage("Şifrenizi sıfırlayabilmeniz için mail adresinize bir bağlantı göndereceğiz. Onaylıyor musunuz ?");
            dialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mAuth.sendPasswordResetEmail(login_mail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                Toast.makeText(LoginActivity.this, "Sıfırlama mailiniz başarıyla gönderildi", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(LoginActivity.this, "Hata: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            dialog.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.show();
        }
    }
}