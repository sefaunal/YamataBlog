package com.example.yamatablog;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.yamatablog.Models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    EditText registerMail, registerPassword, registerName;
    AutoCompleteTextView registerDepartment;
    Button registerButton;
    ProgressBar registerBar;

    ImageView profilePicture;
    Uri profilePictureURI;

    FirebaseAuth mAuth;
    FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        registerName = findViewById(R.id.register_name);
        registerMail = findViewById(R.id.register_mail);
        registerPassword = findViewById(R.id.register_password);
        registerButton = findViewById(R.id.register_button);
        registerBar = findViewById(R.id.register_progress);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUserAccount();
            }
        });

        registerDepartment = findViewById(R.id.register_department);
        ArrayAdapter<String>adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dropdownList);
        registerDepartment.setAdapter(adapter);
        registerDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerDepartment.showDropDown();
            }
        });

        profilePicture = findViewById(R.id.register_image);
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        profilePictureURI = data.getData();
                        profilePicture.setImageURI(profilePictureURI);
                    }
                }
            });
    private void openGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryLauncher.launch(galleryIntent);
    }

    private void createUserAccount(){
        if(TextUtils.isEmpty(registerName.getText().toString())){
            registerName.setError("Lütfen adınızı girin");
            registerName.requestFocus();
        }
        else if(TextUtils.isEmpty(registerMail.getText().toString())){
            registerMail.setError("Lütfen mail adresinizi girin");
            registerMail.requestFocus();
        }
        else if(TextUtils.isEmpty(registerPassword.getText().toString())){
            registerPassword.setError("Lütfen şifrenizi girin");
            registerPassword.requestFocus();
        }
        else if(TextUtils.isEmpty(registerDepartment.getText().toString())){
            registerDepartment.setError("Lütfen çalıştığınız bölümü seçin");
            registerDepartment.requestFocus();
        }
        else if(profilePictureURI == null){
            Toast.makeText(RegisterActivity.this, "Lütfen profil fotoğrafınızı seçin", Toast.LENGTH_SHORT).show();
        }
        else{
            registerButton.setVisibility(View.INVISIBLE);
            registerBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(registerMail.getText().toString(), registerPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        firebaseMessaging.subscribeToTopic("notifications");
                        updateUserAccount();
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Hata: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        registerButton.setVisibility(View.VISIBLE);
                        registerBar.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }
    private void updateUserAccount(){
        StorageReference profilePicturePath = FirebaseStorage.getInstance().getReference().child("ProfilePhotos").child(mAuth.getCurrentUser().getUid()+"_profilePicture");
        profilePicturePath.putFile(profilePictureURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profilePicturePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("UserDetails").child(mAuth.getCurrentUser().getUid());
                        UserDetails userDetails = new UserDetails(mAuth.getCurrentUser().getUid(), registerName.getText().toString(), registerMail.getText().toString(), registerDepartment.getText().toString(), uri.toString(), registerName.getText().toString().toLowerCase(), "Çevrimdışı");
                        myRef.setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mAuth.signOut();
                                Toast.makeText(RegisterActivity.this, "Kaydınız Başarıyla Tamamlandı", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }
    private static final String[] dropdownList = new String[]{"Bilgi Teknolojileri", "Lojistik", "Şantiye Şefi", "İnsan Kaynakları", "Teknik Ofis"};
}