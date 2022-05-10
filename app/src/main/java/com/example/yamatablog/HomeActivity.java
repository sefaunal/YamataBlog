package com.example.yamatablog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.yamatablog.Adapters.PopupImageAdapter;
import com.example.yamatablog.Models.Post;
import com.example.yamatablog.Models.PostImages;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yamatablog.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String userName, userMail, userPhotoUrl;
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("UserDetails").child(mAuth.getCurrentUser().getUid());

    Dialog popAddPost;
    ImageView popupUserImage, popupAddBtn;
    TextView popupTitle, popupDescription, popupAddress;
    AutoCompleteTextView popupDepartment;
    ProgressBar popupBar;
    Uri popupPostImageUri;
    RecyclerView popupPostImage;
    Button popupAddPhotoButton;

    String lastSeen;

    List<Uri>PostListImage;
    PopupImageAdapter ImageAdapter;
    int maxIndex = 0;
    String x;

    StorageReference postPhotoList;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initiatePopup();

        setSupportActionBar(binding.appBarHome.toolbar);
        binding.appBarHome.fab.setOnClickListener(view -> popAddPost.show());

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            String token = task.getResult();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userToken", token);
            userRef.updateChildren(hashMap);
        });

        FirebaseMessaging subscribe = FirebaseMessaging.getInstance();
        subscribe.subscribeToTopic("notifications");

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home_notfixed, R.id.nav_home_fixed)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {logout();
        return true;
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("userName").getValue(String.class);
                userMail = snapshot.child("userMail").getValue(String.class);
                userPhotoUrl = snapshot.child("userPhoto").getValue(String.class);
                updateNavHeader();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void logout(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
        dialog.setMessage("Çıkış yapmak istediğinize emin misiniz?");
        dialog.setPositiveButton("Evet", (dialogInterface, i) -> {
            lastSeen = "Son Görülme : " + ServerValueToTime();
            statusUpdate("offline", lastSeen);
            mAuth.signOut();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        });
        dialog.setNegativeButton("Hayır", (dialogInterface, i) -> { });
        dialog.show();
    }

    private void updateNavHeader(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        ImageView navUserPhoto = headerView.findViewById(R.id.nav_user_photo);

        navUserName.setText(userName);
        navUserMail.setText(userMail);
        Glide.with(this).load(userPhotoUrl).into(navUserPhoto);
    }

    private void initiatePopup(){
        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        popupUserImage = popAddPost.findViewById(R.id.popup_user_image);
        popupPostImage = popAddPost.findViewById(R.id.popup_img_RV);
        popupPostImage.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
        popupPostImage.setHasFixedSize(true);
        popupPostImage.setVisibility(View.GONE);



        popupAddBtn = popAddPost.findViewById(R.id.post_detail_comment_add);
        popupAddBtn.setVisibility(View.GONE);

        popupBar = popAddPost.findViewById(R.id.popup_progressBar);
        popupTitle = popAddPost.findViewById(R.id.popup_title);
        popupDescription = popAddPost.findViewById(R.id.popup_description);
        popupDepartment = popAddPost.findViewById(R.id.popup_dropdown);
        popupAddress = popAddPost.findViewById(R.id.popup_address);
        popupAddPhotoButton = popAddPost.findViewById(R.id.popup_addPhoto_button);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dropdownList);
        popupDepartment.setAdapter(adapter);
        popupDepartment.setOnClickListener(view -> popupDepartment.showDropDown());
        popupAddPhotoButton.setOnClickListener(view -> openGallery());
        Glide.with(HomeActivity.this).load(userPhotoUrl).into(popupUserImage);

        popupAddBtn.setOnClickListener(view -> {
            if (TextUtils.isEmpty(popupTitle.getText().toString()) || TextUtils.isEmpty(popupDescription.getText().toString()) || TextUtils.isEmpty(popupDepartment.getText().toString()) || TextUtils.isEmpty(popupAddress.getText().toString()) || popupPostImageUri == null){
                Toast.makeText(HomeActivity.this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            }
            else{
                popupAddBtn.setVisibility(View.INVISIBLE);
                popupBar.setVisibility(View.VISIBLE);
                createPost();
            }
        });
    }

    private void createPost() {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts").push();
        StorageReference popupImagePath = FirebaseStorage.getInstance().getReference().child("PostImages").child(postRef.getKey() + "_PostImage");
        popupImagePath.putFile(popupPostImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                popupImagePath.getDownloadUrl().addOnSuccessListener(uri -> {
                    Post newPost = new Post(
                            mAuth.getCurrentUser().getUid(),
                            popupTitle.getText().toString(),
                            popupAddress.getText().toString(),
                            popupDepartment.getText().toString(),
                            popupDescription.getText().toString(),
                            uri.toString()
                    );
                    newPost.setPostID(postRef.getKey());
                    postRef.setValue(newPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(HomeActivity.this, "İletiniz Başarıyla Paylaşıldı", Toast.LENGTH_SHORT).show();
                            popupAddBtn.setVisibility(View.VISIBLE);
                            popupBar.setVisibility(View.INVISIBLE);

                            popAddPost.dismiss();
                            initiatePopup();
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(HomeActivity.this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        popupAddBtn.setVisibility(View.VISIBLE);
                        popupBar.setVisibility(View.INVISIBLE);
                    });

                }).addOnFailureListener(e -> {
                    Toast.makeText(HomeActivity.this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupBar.setVisibility(View.INVISIBLE);
                });
            }
        });

        for (int i = 0; i < maxIndex; i++) {
            int check = i;
            postPhotoList = FirebaseStorage.getInstance().getReference().child("PostImages").child(postRef.getKey() + "_PostImage_" + i);
            postPhotoList.putFile(PostListImage.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference mPRef = FirebaseStorage.getInstance().getReference("PostImages").child(postRef.getKey() + "_PostImage_" + check);
                    mPRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            x = uri.toString();
                            sendToDatabase(x, postRef.getKey());
                        }
                    });
                }
            });
        }
    }

    private void sendToDatabase(String x, String postKey) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PostPhotos").child(postKey).push();
        PostImages postImages = new PostImages(x);
        databaseReference.setValue(postImages);

    }

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        if (result.getData().getClipData() != null){
                            maxIndex = result.getData().getClipData().getItemCount();
                            if (maxIndex > 5){
                                Toast.makeText(HomeActivity.this, "En fazla 5 Adet Fotoğraf Seçebilirsiniz", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                popupPostImage.setVisibility(View.VISIBLE);
                                popupAddBtn.setVisibility(View.VISIBLE);

                                PostListImage = new ArrayList<>();
                                for (int i = 0; i < maxIndex; i++){
                                    PostListImage.add(result.getData().getClipData().getItemAt(i).getUri());
                                }
                                ImageAdapter = new PopupImageAdapter(HomeActivity.this, PostListImage);
                                popupPostImage.setAdapter(ImageAdapter);
                                popupPostImageUri = result.getData().getClipData().getItemAt(0).getUri();
                            }
                        }
                        else if (result.getData() != null){
                            popupPostImage.setVisibility(View.VISIBLE);
                            popupAddBtn.setVisibility(View.VISIBLE);

                            PostListImage = new ArrayList<>();
                            PostListImage.add(result.getData().getData());
                            ImageAdapter = new PopupImageAdapter(HomeActivity.this, PostListImage);
                            popupPostImage.setAdapter(ImageAdapter);
                            popupPostImageUri = result.getData().getData();
                            maxIndex = 1;
                        }
                    }
                }
            });
    private void openGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryLauncher.launch(galleryIntent);
    }



    private void statusUpdate(String status, String lastSeen){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userStatus", status);
        hashMap.put("userLastSeen", lastSeen);

        userRef.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusUpdate("online", "Çevrimiçi");
    }

    @Override
    protected void onPause() {
        super.onPause();
        lastSeen = "Son Görülme : " + ServerValueToTime();

        statusUpdate("offline", lastSeen);
    }

    private String ServerValueToTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    private static final String[] dropdownList = new String[]{"Bilgi Teknolojileri", "Lojistik", "Şantiye Şefi", "İnsan Kaynakları", "Teknik Ofis"};
}